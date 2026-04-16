package com.deicmar.checklist.repository;

import com.deicmar.checklist.model.entity.Empilhadeira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpilhadeiraRepository extends JpaRepository<Empilhadeira, Long> {
    
    List<Empilhadeira> findByAtivaTrue();
    
    List<Empilhadeira> findByBloqueadaTrue();
    
    List<Empilhadeira> findByBloqueadaFalseAndAtivaTrue();
    
    List<Empilhadeira> findByModelo(String modelo);
    
    List<Empilhadeira> findByTipo(String tipo);
}
