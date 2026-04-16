package com.deicmar.checklist.service;

import com.deicmar.checklist.dto.empilhadeira.BloquearEmpilhadeiraRequest;
import com.deicmar.checklist.dto.empilhadeira.EmpilhadeiraRequest;
import com.deicmar.checklist.dto.empilhadeira.EmpilhadeiraResponse;
import com.deicmar.checklist.exception.BusinessException;
import com.deicmar.checklist.exception.ResourceNotFoundException;
import com.deicmar.checklist.mapper.EmpilhadeiraMapper;
import com.deicmar.checklist.model.entity.Empilhadeira;
import com.deicmar.checklist.repository.EmpilhadeiraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpilhadeiraService {

    private final EmpilhadeiraRepository empilhadeiraRepository;
    private final EmpilhadeiraMapper empilhadeiraMapper;
    private final DashboardEventService eventService;

    @Transactional
    public EmpilhadeiraResponse criar(EmpilhadeiraRequest request) {
        Empilhadeira emp = empilhadeiraMapper.toEntity(request);
        Empilhadeira salva = empilhadeiraRepository.save(emp);
        log.info("Empilhadeira criada — ID: {}, Modelo: {}", salva.getId(), salva.getModelo());
        eventService.publicar("empilhadeira_criada",
                Map.of("id", salva.getId(), "modelo", salva.getModelo(), "status", "DISPONIVEL"));
        return empilhadeiraMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<EmpilhadeiraResponse> listarTodas() {
        return empilhadeiraRepository.findAll().stream()
                .map(empilhadeiraMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmpilhadeiraResponse> listarAtivas() {
        return empilhadeiraRepository.findByAtivaTrue().stream()
                .map(empilhadeiraMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmpilhadeiraResponse> listarDisponiveis() {
        return empilhadeiraRepository.findByBloqueadaFalseAndAtivaTrue().stream()
                .map(empilhadeiraMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmpilhadeiraResponse> listarBloqueadas() {
        return empilhadeiraRepository.findByBloqueadaTrue().stream()
                .map(empilhadeiraMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpilhadeiraResponse buscarPorId(Long id) {
        return empilhadeiraMapper.toResponse(buscar(id));
    }

    @Transactional
    public EmpilhadeiraResponse bloquear(Long id, BloquearEmpilhadeiraRequest request) {
        Empilhadeira emp = buscar(id);
        if (emp.getBloqueada()) throw new BusinessException("Empilhadeira já está bloqueada");
        emp.setBloqueada(true);
        emp.setMotivoBloqueio(request.getMotivo());
        Empilhadeira atualizada = empilhadeiraRepository.save(emp);
        log.info("Empilhadeira bloqueada — ID: {}", id);
        // Publica evento SSE — painel atualiza em tempo real
        eventService.publicar("empilhadeira_bloqueada", Map.of(
                "id", id, "modelo", emp.getModelo(),
                "motivo", request.getMotivo(), "status", "BLOQUEADA"));
        return empilhadeiraMapper.toResponse(atualizada);
    }

    @Transactional
    public EmpilhadeiraResponse desbloquear(Long id) {
        Empilhadeira emp = buscar(id);
        if (!emp.getBloqueada()) throw new BusinessException("Empilhadeira não está bloqueada");
        emp.setBloqueada(false);
        emp.setMotivoBloqueio(null);
        Empilhadeira atualizada = empilhadeiraRepository.save(emp);
        log.info("Empilhadeira desbloqueada — ID: {}", id);
        eventService.publicar("empilhadeira_desbloqueada", Map.of(
                "id", id, "modelo", emp.getModelo(), "status", "DISPONIVEL"));
        return empilhadeiraMapper.toResponse(atualizada);
    }

    @Transactional
    public void inativar(Long id) {
        Empilhadeira emp = buscar(id);
        emp.setAtiva(false);
        empilhadeiraRepository.save(emp);
        log.info("Empilhadeira inativada — ID: {}", id);
        eventService.publicar("empilhadeira_inativada", Map.of(
                "id", id, "modelo", emp.getModelo(), "status", "INATIVA"));
    }

    private Empilhadeira buscar(Long id) {
        return empilhadeiraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empilhadeira não encontrada com ID: " + id));
    }
}
