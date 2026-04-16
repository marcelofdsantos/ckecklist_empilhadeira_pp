package com.deicmar.checklist.repository;

import com.deicmar.checklist.model.entity.Checklist;
import com.deicmar.checklist.model.enums.ResultadoChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    
    List<Checklist> findByEmpilhadeiraId(Long empilhadeiraId);
    
    List<Checklist> findByOperadorId(Long operadorId);
    
    List<Checklist> findByData(LocalDate data);
    
    @Query("SELECT c FROM Checklist c WHERE c.data BETWEEN :dataInicio AND :dataFim ORDER BY c.data DESC, c.horaVistoria DESC")
    List<Checklist> findByDataBetween(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    List<Checklist> findByResultado(ResultadoChecklist resultado);
    
    @Query("SELECT c FROM Checklist c WHERE c.empilhadeira.id = :empilhadeiraId AND c.data = :data")
    List<Checklist> findByEmpilhadeiraIdAndData(@Param("empilhadeiraId") Long empilhadeiraId, @Param("data") LocalDate data);
    
    @Query("SELECT c FROM Checklist c ORDER BY c.data DESC, c.horaVistoria DESC")
    List<Checklist> findAllOrderByDataDesc();
}
