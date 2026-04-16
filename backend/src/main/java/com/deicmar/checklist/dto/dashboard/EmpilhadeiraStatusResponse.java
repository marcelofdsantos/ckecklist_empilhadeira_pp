package com.deicmar.checklist.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpilhadeiraStatusResponse {
    private Long id;
    private String modelo;
    private String tipo;
    private Integer capacidade;
    private String status; // DISPONIVEL, BLOQUEADA, INATIVA
    private String motivoBloqueio;
    private LocalDateTime atualizadoEm;
    // Último checklist
    private String ultimoChecklistData;
    private String ultimoChecklistResultado;
    private String ultimoOperador;
}
