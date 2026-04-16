package com.deicmar.checklist.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistRecenteResponse {
    private Long id;
    private String empilhadeiraModelo;
    private String operadorNome;
    private String data;
    private String horaVistoria;
    private String turno;
    private String resultado;
}
