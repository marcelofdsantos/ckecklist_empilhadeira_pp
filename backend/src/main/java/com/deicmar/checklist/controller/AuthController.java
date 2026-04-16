package com.deicmar.checklist.controller;

import com.deicmar.checklist.dto.auth.LoginRequest;
import com.deicmar.checklist.dto.auth.LoginResponse;
import com.deicmar.checklist.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e geração de token JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Realizar login",
        description = "Autentica com RE e senha. Retorna JWT Bearer token válido por 24h. " +
                      "Use o token no header: Authorization: Bearer {token}"
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
