package com.deicmar.checklist.controller;

import com.deicmar.checklist.dto.checklist.ChecklistRequest;
import com.deicmar.checklist.dto.checklist.ChecklistResponse;
import com.deicmar.checklist.exception.BusinessException;
import com.deicmar.checklist.repository.UsuarioRepository;
import com.deicmar.checklist.security.Roles;
import com.deicmar.checklist.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checklists")
@RequiredArgsConstructor
@Tag(name = "Checklists", description = "Criação e consulta de vistorias")
public class ChecklistController {

    private final ChecklistService checklistService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR_OPERACIONAL','SUPERVISOR','OPERADOR')")
    public ResponseEntity<ChecklistResponse> criar(
            @Valid @RequestBody ChecklistRequest request,
            Authentication auth) {

        // IDOR: operador só cria checklist para si mesmo
        if (hasRole(auth, "OPERADOR")) {
            Long userId = getUsuarioId(auth.getName());
            if (!request.getOperadorId().equals(userId)) {
                throw new BusinessException("Operador não pode criar checklist para outro usuário");
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checklistService.criar(request));
    }

    @GetMapping
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<List<ChecklistResponse>> listarTodos() {
        return ResponseEntity.ok(checklistService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_MECANICA','SUPERVISOR_OPERACIONAL','SUPERVISOR','OPERADOR')")
    public ResponseEntity<ChecklistResponse> buscarPorId(
            @PathVariable Long id, Authentication auth) {

        ChecklistResponse c = checklistService.buscarPorId(id);

        // IDOR: operador só vê seus próprios checklists
        if (hasRole(auth, "OPERADOR")) {
            Long userId = getUsuarioId(auth.getName());
            if (!c.getOperador().getId().equals(userId)) {
                throw new BusinessException("Acesso negado a este checklist");
            }
        }

        return ResponseEntity.ok(c);
    }

    @GetMapping("/empilhadeira/{empilhadeiraId}")
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<List<ChecklistResponse>> listarPorEmpilhadeira(
            @PathVariable Long empilhadeiraId) {
        return ResponseEntity.ok(checklistService.listarPorEmpilhadeira(empilhadeiraId));
    }

    @GetMapping("/operador/{operadorId}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_MECANICA','SUPERVISOR_OPERACIONAL','SUPERVISOR','OPERADOR')")
    public ResponseEntity<List<ChecklistResponse>> listarPorOperador(
            @PathVariable Long operadorId, Authentication auth) {

        // IDOR: operador só vê seus próprios checklists
        if (hasRole(auth, "OPERADOR")) {
            Long userId = getUsuarioId(auth.getName());
            if (!operadorId.equals(userId)) {
                throw new BusinessException("Operador só pode consultar seus próprios checklists");
            }
        }

        return ResponseEntity.ok(checklistService.listarPorOperador(operadorId));
    }

    @GetMapping("/data/{data}")
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<List<ChecklistResponse>> listarPorData(@PathVariable String data) {
        return ResponseEntity.ok(checklistService.listarPorData(data));
    }

    @GetMapping("/periodo")
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<List<ChecklistResponse>> listarPorPeriodo(
            @RequestParam String dataInicio, @RequestParam String dataFim) {
        return ResponseEntity.ok(checklistService.listarPorPeriodo(dataInicio, dataFim));
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private Long getUsuarioId(String re) {
        return usuarioRepository.findByRe(re)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"))
                .getId();
    }
}
