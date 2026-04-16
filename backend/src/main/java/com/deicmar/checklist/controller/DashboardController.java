package com.deicmar.checklist.controller;

import com.deicmar.checklist.dto.dashboard.*;
import com.deicmar.checklist.service.DashboardEventService;
import com.deicmar.checklist.service.DashboardService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Painel admin em tempo real")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardEventService eventService;

    /** SSE — stream de eventos em tempo real para o painel admin */
    @GetMapping(value = "/eventos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_MECANICA','SUPERVISOR_OPERACIONAL','SUPERVISOR')")
    public SseEmitter streamEventos() {
        return eventService.subscribe();
    }

    /** Resumo geral dos cards do dashboard */
    @GetMapping("/resumo")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_MECANICA','SUPERVISOR_OPERACIONAL','SUPERVISOR')")
    public ResponseEntity<DashboardResumoResponse> getResumo() {
        return ResponseEntity.ok(dashboardService.getResumo());
    }

    /** Status de todas as empilhadeiras — ADMIN e GERENTE_MECANICA */
    @GetMapping("/empilhadeiras/status")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_MECANICA')")
    public ResponseEntity<List<EmpilhadeiraStatusResponse>> getStatusEmpilhadeiras() {
        return ResponseEntity.ok(dashboardService.getStatusEmpilhadeiras());
    }

    /** Apenas bloqueadas — SUPERVISOR_OPERACIONAL também pode ver */
    @GetMapping("/empilhadeiras/bloqueadas")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_MECANICA','SUPERVISOR_OPERACIONAL','SUPERVISOR')")
    public ResponseEntity<List<EmpilhadeiraStatusResponse>> getBloqueadas() {
        return ResponseEntity.ok(dashboardService.getEmpilhadeirasBloqueadas());
    }

    /** Últimos N checklists */
    @GetMapping("/checklists/recentes")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR_OPERACIONAL','SUPERVISOR')")
    public ResponseEntity<List<ChecklistRecenteResponse>> getRecentes(
            @RequestParam(defaultValue = "20") int limite) {
        return ResponseEntity.ok(dashboardService.getChecklistsRecentes(limite));
    }

    /** Checklists agrupados por turno — para uma data específica ou hoje */
    @GetMapping("/checklists/por-turno")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR_OPERACIONAL','SUPERVISOR')")
    public ResponseEntity<List<ChecklistPorTurnoResponse>> getPorTurno(
            @RequestParam(required = false) String data) {
        return ResponseEntity.ok(dashboardService.getChecklistsPorTurno(data));
    }
}
