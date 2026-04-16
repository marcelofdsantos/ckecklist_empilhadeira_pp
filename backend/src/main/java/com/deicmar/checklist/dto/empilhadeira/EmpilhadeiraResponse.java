package com.deicmar.checklist.dto.empilhadeira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpilhadeiraResponse {
    
    private Long id;
    private String modelo;
    private String tipo;
    private Integer capacidade;
    private Boolean bloqueada;
    private String motivoBloqueio;
    private Boolean ativa;
}
