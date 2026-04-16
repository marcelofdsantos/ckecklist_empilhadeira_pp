package com.deicmar.checklist.mapper;

import com.deicmar.checklist.dto.usuario.UsuarioRequest;
import com.deicmar.checklist.dto.usuario.UsuarioResponse;
import com.deicmar.checklist.model.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .re(usuario.getRe())
                .nomeCompleto(usuario.getNomeCompleto())
                .perfil(usuario.getPerfil())
                .ativo(usuario.getAtivo())
                .build();
    }

    public Usuario toEntity(UsuarioRequest request) {
        if (request == null) {
            return null;
        }
        
        return Usuario.builder()
                .re(request.getRe())
                .nomeCompleto(request.getNomeCompleto())
                .senha(request.getSenha()) // Será encriptada no service
                .perfil(request.getPerfil())
                .ativo(true)
                .build();
    }
}
