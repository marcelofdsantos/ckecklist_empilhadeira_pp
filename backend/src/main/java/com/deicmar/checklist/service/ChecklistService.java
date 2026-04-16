package com.deicmar.checklist.service;

import com.deicmar.checklist.dto.checklist.ChecklistRequest;
import com.deicmar.checklist.dto.checklist.ChecklistResponse;
import com.deicmar.checklist.dto.checklist.ItemChecklistRequest;
import com.deicmar.checklist.exception.BusinessException;
import com.deicmar.checklist.exception.ResourceNotFoundException;
import com.deicmar.checklist.mapper.ChecklistMapper;
import com.deicmar.checklist.model.entity.Checklist;
import com.deicmar.checklist.model.entity.Empilhadeira;
import com.deicmar.checklist.model.entity.ItemChecklist;
import com.deicmar.checklist.model.entity.Usuario;
import com.deicmar.checklist.model.enums.ResultadoChecklist;
import com.deicmar.checklist.model.enums.StatusItem;
import com.deicmar.checklist.model.enums.TipoItem;
import com.deicmar.checklist.repository.ChecklistRepository;
import com.deicmar.checklist.repository.EmpilhadeiraRepository;
import com.deicmar.checklist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChecklistService {

    // BUG FIX: formatter como constante — evita recriar objeto a cada requisição
    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm")
            .optionalStart().appendPattern(":ss").optionalEnd()
            .toFormatter();

    private final ChecklistRepository checklistRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpilhadeiraRepository empilhadeiraRepository;
    private final ChecklistMapper checklistMapper;
    private final DashboardEventService eventService;

    @Transactional
    public ChecklistResponse criar(ChecklistRequest request) {
        log.debug("Criando checklist para empilhadeira ID: {}", request.getEmpilhadeiraId());

        Usuario operador = usuarioRepository.findById(request.getOperadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operador não encontrado com ID: " + request.getOperadorId()));

        if (!operador.getAtivo()) {
            throw new BusinessException("Operador inativo");
        }

        Empilhadeira empilhadeira = empilhadeiraRepository.findById(request.getEmpilhadeiraId())
                .orElseThrow(() -> new ResourceNotFoundException("Empilhadeira não encontrada com ID: " + request.getEmpilhadeiraId()));

        if (!empilhadeira.getAtiva()) {
            throw new BusinessException("Empilhadeira inativa");
        }

        if (empilhadeira.getBloqueada()) {
            throw new BusinessException("Empilhadeira bloqueada: " + empilhadeira.getMotivoBloqueio());
        }

        if (request.getHorimetroFinal() != null && request.getHorimetroFinal() < request.getHorimetroInicial()) {
            throw new BusinessException("Horímetro final não pode ser menor que o inicial");
        }

        LocalDate data;
        try {
            data = LocalDate.parse(request.getData());
        } catch (DateTimeParseException ex) {
            throw new BusinessException("Formato de data inválido. Use YYYY-MM-DD");
        }

        LocalTime horaVistoria;
        try {
            horaVistoria = LocalTime.parse(request.getHoraVistoria(), TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new BusinessException("Formato de hora inválido. Use HH:mm ou HH:mm:ss");
        }

        List<Checklist> existentesNoDia = checklistRepository.findByEmpilhadeiraIdAndData(empilhadeira.getId(), data);
        if (existentesNoDia != null && !existentesNoDia.isEmpty()) {
            throw new BusinessException("Já existe checklist para a empilhadeira ID "
                    + empilhadeira.getId() + " na data " + data);
        }

        ResultadoChecklist resultado = calcularResultado(request.getItens());

        Checklist checklist = Checklist.builder()
                .data(data)
                .horaVistoria(horaVistoria)
                .turno(request.getTurno())
                .horimetroInicial(request.getHorimetroInicial())
                .horimetroFinal(request.getHorimetroFinal())
                .operador(operador)
                .empilhadeira(empilhadeira)
                .resultado(resultado)
                .observacaoGeral(request.getObservacaoGeral())
                .build();

        for (ItemChecklistRequest itemRequest : request.getItens()) {
            ItemChecklist item = ItemChecklist.builder()
                    .descricao(itemRequest.getDescricao())
                    .tipo(itemRequest.getTipo())
                    .status(itemRequest.getStatus())
                    .observacao(itemRequest.getObservacao())
                    .build();
            checklist.adicionarItem(item);
        }

        // BUG FIX: removido try/catch genérico — ele engolia RuntimeException e
        // impedia o rollback do @Transactional. Deixar propagar naturalmente.
        Checklist checklistSalvo = checklistRepository.save(checklist);

        if (resultado == ResultadoChecklist.REPROVADO) {
            empilhadeira.setBloqueada(true);
            empilhadeira.setMotivoBloqueio("Checklist reprovado em " + data);
            empilhadeiraRepository.save(empilhadeira);
            log.info("Empilhadeira ID {} bloqueada devido a checklist reprovado", empilhadeira.getId());
        }

        log.info("Checklist criado com sucesso. ID: {}, Resultado: {}", checklistSalvo.getId(), resultado);

        // Publica evento SSE para o dashboard em tempo real
        eventService.publicar("checklist_salvo", Map.of(
                "checklistId", checklistSalvo.getId(),
                "empilhadeiraId", empilhadeira.getId(),
                "modelo", empilhadeira.getModelo(),
                "resultado", resultado.name(),
                "operador", operador.getNomeCompleto(),
                "data", data.toString()
        ));

        return checklistMapper.toResponse(checklistSalvo);
    }

    private ResultadoChecklist calcularResultado(List<ItemChecklistRequest> itens) {
        boolean temItemImpeditivo = itens.stream()
                .anyMatch(item -> item.getTipo() == TipoItem.IMPEDITIVO
                        && item.getStatus() == StatusItem.NAO_CONFORME);
        return temItemImpeditivo ? ResultadoChecklist.REPROVADO : ResultadoChecklist.APROVADO;
    }

    @Transactional(readOnly = true)
    public List<ChecklistResponse> listarTodos() {
        return checklistRepository.findAllOrderByDataDesc().stream()
                .map(checklistMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChecklistResponse buscarPorId(Long id) {
        return checklistRepository.findById(id)
                .map(checklistMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<ChecklistResponse> listarPorEmpilhadeira(Long empilhadeiraId) {
        if (!empilhadeiraRepository.existsById(empilhadeiraId)) {
            throw new ResourceNotFoundException("Empilhadeira não encontrada com ID: " + empilhadeiraId);
        }
        return checklistRepository.findByEmpilhadeiraId(empilhadeiraId).stream()
                .map(checklistMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChecklistResponse> listarPorOperador(Long operadorId) {
        if (!usuarioRepository.existsById(operadorId)) {
            throw new ResourceNotFoundException("Operador não encontrado com ID: " + operadorId);
        }
        return checklistRepository.findByOperadorId(operadorId).stream()
                .map(checklistMapper::toResponse)
                .collect(Collectors.toList());
    }

    // BUG FIX: DateTimeParseException tratada — antes causava HTTP 500
    @Transactional(readOnly = true)
    public List<ChecklistResponse> listarPorData(String data) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(data);
        } catch (DateTimeParseException ex) {
            throw new BusinessException("Formato de data inválido. Use YYYY-MM-DD");
        }
        return checklistRepository.findByData(localDate).stream()
                .map(checklistMapper::toResponse)
                .collect(Collectors.toList());
    }

    // BUG FIX: DateTimeParseException tratada em listarPorPeriodo também
    @Transactional(readOnly = true)
    public List<ChecklistResponse> listarPorPeriodo(String dataInicio, String dataFim) {
        LocalDate inicio;
        LocalDate fim;
        try {
            inicio = LocalDate.parse(dataInicio);
            fim = LocalDate.parse(dataFim);
        } catch (DateTimeParseException ex) {
            throw new BusinessException("Formato de data inválido. Use YYYY-MM-DD");
        }

        if (fim.isBefore(inicio)) {
            throw new BusinessException("Data fim não pode ser anterior à data início");
        }

        return checklistRepository.findByDataBetween(inicio, fim).stream()
                .map(checklistMapper::toResponse)
                .collect(Collectors.toList());
    }
}
