package com.deicmar.checklist.dto.auth;

import com.deicmar.checklist.model.enums.Perfil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    
    @Builder.Default
    private String tipo = "Bearer";
    
    private Long usuarioId;
    private String re;
    private String nomeCompleto;
    private Perfil perfil;
}
