package com.deicmar.checklist.controller;

import com.deicmar.checklist.dto.usuario.AtualizarSenhaRequest;
import com.deicmar.checklist.dto.usuario.UsuarioRequest;
import com.deicmar.checklist.dto.usuario.UsuarioResponse;
import com.deicmar.checklist.model.enums.Perfil;
import com.deicmar.checklist.security.Roles;
import com.deicmar.checklist.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários e perfis")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize(Roles.ADMIN_ONLY)
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(request));
    }

    @GetMapping
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/ativos")
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<List<UsuarioResponse>> listarAtivos() {
        return ResponseEntity.ok(usuarioService.listarAtivos());
    }

    @GetMapping("/perfil/{perfil}")
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<List<UsuarioResponse>> listarPorPerfil(@PathVariable Perfil perfil) {
        return ResponseEntity.ok(usuarioService.listarPorPerfil(perfil));
    }

    @GetMapping("/{id}")
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/re/{re}")
    @PreAuthorize(Roles.PAINEL)
    public ResponseEntity<UsuarioResponse> buscarPorRe(@PathVariable String re) {
        return ResponseEntity.ok(usuarioService.buscarPorRe(re));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(Roles.ADMIN_ONLY)
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/senha")
    @PreAuthorize(Roles.ADMIN_ONLY)
    public ResponseEntity<UsuarioResponse> atualizarSenha(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarSenhaRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarSenha(id, request.getNovaSenha()));
    }
}
