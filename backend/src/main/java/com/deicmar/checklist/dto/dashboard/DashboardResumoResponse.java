package com.deicmar.checklist.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumoResponse {
    private long totalEmpilhadeiras;
    private long empilhadeirasDisponiveis;
    private long empilhadeirasBloqueadas;
    private long empilhadeirasInativas;
    private long checklistsHoje;
    private long checklistsAprovadosHoje;
    private long checklistsReprovadosHoje;
    private long operadoresAtivos;
}
