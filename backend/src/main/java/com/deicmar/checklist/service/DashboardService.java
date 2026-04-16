package com.deicmar.checklist.service;

import com.deicmar.checklist.dto.dashboard.*;
import com.deicmar.checklist.model.entity.Checklist;
import com.deicmar.checklist.model.entity.Empilhadeira;
import com.deicmar.checklist.model.enums.Perfil;
import com.deicmar.checklist.model.enums.ResultadoChecklist;
import com.deicmar.checklist.model.enums.Turno;
import com.deicmar.checklist.repository.ChecklistRepository;
import com.deicmar.checklist.repository.EmpilhadeiraRepository;
import com.deicmar.checklist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final EmpilhadeiraRepository empilhadeiraRepository;
    private final ChecklistRepository checklistRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public DashboardResumoResponse getResumo() {
        LocalDate hoje = LocalDate.now();

        long total      = empilhadeiraRepository.count();
        long bloqueadas = empilhadeiraRepository.findByBloqueadaTrue().size();
        long inativas   = empilhadeiraRepository.findAll().stream()
                .filter(e -> !e.getAtiva()).count();
        long disponiveis = empilhadeiraRepository.findByBloqueadaFalseAndAtivaTrue().size();

        List<Checklist> hoje_checklists = checklistRepository.findByData(hoje);
        long aprovadosHoje  = hoje_checklists.stream()
                .filter(c -> c.getResultado() == ResultadoChecklist.APROVADO).count();
        long reprovadosHoje = hoje_checklists.stream()
                .filter(c -> c.getResultado() == ResultadoChecklist.REPROVADO).count();

        long operadores = usuarioRepository.findByPerfilAndAtivoTrue(Perfil.OPERADOR).size();

        return DashboardResumoResponse.builder()
                .totalEmpilhadeiras(total)
                .empilhadeirasDisponiveis(disponiveis)
                .empilhadeirasBloqueadas(bloqueadas)
                .empilhadeirasInativas(inativas)
                .checklistsHoje(hoje_checklists.size())
                .checklistsAprovadosHoje(aprovadosHoje)
                .checklistsReprovadosHoje(reprovadosHoje)
                .operadoresAtivos(operadores)
                .build();
    }

    @Transactional(readOnly = true)
    public List<EmpilhadeiraStatusResponse> getStatusEmpilhadeiras() {
        return empilhadeiraRepository.findAll().stream()
                .map(this::toStatusResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmpilhadeiraStatusResponse> getEmpilhadeirasBloqueadas() {
        return empilhadeiraRepository.findByBloqueadaTrue().stream()
                .map(this::toStatusResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChecklistRecenteResponse> getChecklistsRecentes(int limite) {
        return checklistRepository.findAllOrderByDataDesc().stream()
                .limit(limite)
                .map(c -> ChecklistRecenteResponse.builder()
                        .id(c.getId())
                        .empilhadeiraModelo(c.getEmpilhadeira().getModelo())
                        .operadorNome(c.getOperador().getNomeCompleto())
                        .data(c.getData().toString())
                        .horaVistoria(c.getHoraVistoria().toString())
                        .turno(c.getTurno().name())
                        .resultado(c.getResultado().name())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChecklistPorTurnoResponse> getChecklistsPorTurno(String dataStr) {
        LocalDate data = (dataStr != null) ? LocalDate.parse(dataStr) : LocalDate.now();
        List<Checklist> checklists = checklistRepository.findByData(data);

        Map<Turno, List<Checklist>> porTurno = checklists.stream()
                .collect(Collectors.groupingBy(Checklist::getTurno));

        return List.of(Turno.values()).stream()
                .map(turno -> {
                    List<Checklist> lista = porTurno.getOrDefault(turno, List.of());
                    long aprovados  = lista.stream().filter(c -> c.getResultado() == ResultadoChecklist.APROVADO).count();
                    long reprovados = lista.stream().filter(c -> c.getResultado() == ResultadoChecklist.REPROVADO).count();
                    return ChecklistPorTurnoResponse.builder()
                            .turno(turno.getDescricao())
                            .aprovados(aprovados)
                            .reprovados(reprovados)
                            .total(lista.size())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private EmpilhadeiraStatusResponse toStatusResponse(Empilhadeira e) {
        // Pega o checklist mais recente dessa empilhadeira
        List<Checklist> historico = checklistRepository.findByEmpilhadeiraId(e.getId());
        Checklist ultimo = historico.isEmpty() ? null
                : historico.stream()
                        .max((a, b) -> a.getData().compareTo(b.getData()))
                        .orElse(null);

        String status = !e.getAtiva() ? "INATIVA"
                : e.getBloqueada() ? "BLOQUEADA"
                : "DISPONIVEL";

        return EmpilhadeiraStatusResponse.builder()
                .id(e.getId())
                .modelo(e.getModelo())
                .tipo(e.getTipo())
                .capacidade(e.getCapacidade())
                .status(status)
                .motivoBloqueio(e.getMotivoBloqueio())
                .atualizadoEm(e.getAtualizadoEm())
                .ultimoChecklistData(ultimo != null ? ultimo.getData().toString() : null)
                .ultimoChecklistResultado(ultimo != null ? ultimo.getResultado().name() : null)
                .ultimoOperador(ultimo != null ? ultimo.getOperador().getNomeCompleto() : null)
                .build();
    }
}
