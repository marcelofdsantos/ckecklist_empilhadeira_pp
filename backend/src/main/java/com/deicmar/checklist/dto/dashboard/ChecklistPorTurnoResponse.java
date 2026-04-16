package com.deicmar.checklist.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistPorTurnoResponse {
    private String turno;
    private long aprovados;
    private long reprovados;
    private long total;
}
