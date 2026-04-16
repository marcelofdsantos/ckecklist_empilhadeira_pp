package com.deicmar.checklist.repository;

import com.deicmar.checklist.model.entity.Usuario;
import com.deicmar.checklist.model.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByRe(String re);
    
    boolean existsByRe(String re);
    
    List<Usuario> findByPerfil(Perfil perfil);
    
    List<Usuario> findByAtivoTrue();
    
    List<Usuario> findByPerfilAndAtivoTrue(Perfil perfil);
}
