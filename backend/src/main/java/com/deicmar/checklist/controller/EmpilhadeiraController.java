package com.deicmar.checklist.controller;

import com.deicmar.checklist.dto.empilhadeira.BloquearEmpilhadeiraRequest;
import com.deicmar.checklist.dto.empilhadeira.EmpilhadeiraRequest;
import com.deicmar.checklist.dto.empilhadeira.EmpilhadeiraResponse;
import com.deicmar.checklist.security.Roles;
import com.deicmar.checklist.service.EmpilhadeiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empilhadeiras")
@RequiredArgsConstructor
@Tag(name = "Empilhadeiras", description = "Gerenciamento de equipamentos")
public class EmpilhadeiraController {

    private final EmpilhadeiraService empilhadeiraService;

    @PostMapping
    @PreAuthorize(Roles.GESTAO_EMPILHADEIRA)
    public ResponseEntity<EmpilhadeiraResponse> criar(@Valid @RequestBody EmpilhadeiraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empilhadeiraService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<EmpilhadeiraResponse>> listarTodas() {
        return ResponseEntity.ok(empilhadeiraService.listarTodas());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<EmpilhadeiraResponse>> listarAtivas() {
        return ResponseEntity.ok(empilhadeiraService.listarAtivas());
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<EmpilhadeiraResponse>> listarDisponiveis() {
        return ResponseEntity.ok(empilhadeiraService.listarDisponiveis());
    }

    @GetMapping("/bloqueadas")
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<List<EmpilhadeiraResponse>> listarBloqueadas() {
        return ResponseEntity.ok(empilhadeiraService.listarBloqueadas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpilhadeiraResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empilhadeiraService.buscarPorId(id));
    }

    @PatchMapping("/{id}/bloquear")
    @PreAuthorize(Roles.GESTAO_EMPILHADEIRA)
    public ResponseEntity<EmpilhadeiraResponse> bloquear(
            @PathVariable Long id,
            @Valid @RequestBody BloquearEmpilhadeiraRequest request) {
        return ResponseEntity.ok(empilhadeiraService.bloquear(id, request));
    }

    @PatchMapping("/{id}/desbloquear")
    @PreAuthorize(Roles.GESTAO_EMPILHADEIRA)
    public ResponseEntity<EmpilhadeiraResponse> desbloquear(@PathVariable Long id) {
        return ResponseEntity.ok(empilhadeiraService.desbloquear(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(Roles.ADMIN_ONLY)
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        empilhadeiraService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
