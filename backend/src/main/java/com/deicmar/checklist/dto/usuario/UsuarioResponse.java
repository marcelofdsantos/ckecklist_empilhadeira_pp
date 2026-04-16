package com.deicmar.checklist.dto.usuario;

import com.deicmar.checklist.model.enums.Perfil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    
    private Long id;
    private String re;
    private String nomeCompleto;
    private Perfil perfil;
    private Boolean ativo;
}
