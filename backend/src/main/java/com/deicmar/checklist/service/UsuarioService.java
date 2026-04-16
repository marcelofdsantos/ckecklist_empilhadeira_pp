package com.deicmar.checklist.service;

import com.deicmar.checklist.dto.usuario.UsuarioRequest;
import com.deicmar.checklist.dto.usuario.UsuarioResponse;
import com.deicmar.checklist.exception.BusinessException;
import com.deicmar.checklist.exception.ResourceNotFoundException;
import com.deicmar.checklist.mapper.UsuarioMapper;
import com.deicmar.checklist.model.entity.Usuario;
import com.deicmar.checklist.model.enums.Perfil;
import com.deicmar.checklist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        log.debug("Criando usuário com RE: {}", request.getRe());
        
        if (usuarioRepository.existsByRe(request.getRe())) {
            throw new BusinessException("Já existe um usuário com este RE");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso. ID: {}, RE: {}", usuarioSalvo.getId(), usuarioSalvo.getRe());
        
        return usuarioMapper.toResponse(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        log.debug("Listando todos os usuários");
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarAtivos() {
        log.debug("Listando usuários ativos");
        return usuarioRepository.findByAtivoTrue().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarPorPerfil(Perfil perfil) {
        log.debug("Listando usuários por perfil: {}", perfil);
        return usuarioRepository.findByPerfilAndAtivoTrue(perfil).stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        log.debug("Buscando usuário por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorRe(String re) {
        log.debug("Buscando usuário por RE: {}", re);
        Usuario usuario = usuarioRepository.findByRe(re)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com RE: " + re));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void inativar(Long id) {
        log.debug("Inativando usuário ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuário inativado com sucesso. ID: {}", id);
    }

    @Transactional
    public UsuarioResponse atualizarSenha(Long id, String novaSenha) {
        log.debug("Atualizando senha do usuário ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        log.info("Senha atualizada com sucesso. Usuário ID: {}", id);
        
        return usuarioMapper.toResponse(usuarioAtualizado);
    }
}
