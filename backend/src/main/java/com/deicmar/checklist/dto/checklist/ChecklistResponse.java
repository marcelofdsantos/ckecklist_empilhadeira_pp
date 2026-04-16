package com.deicmar.checklist.dto.checklist;

import com.deicmar.checklist.dto.empilhadeira.EmpilhadeiraResponse;
import com.deicmar.checklist.dto.usuario.UsuarioResponse;
import com.deicmar.checklist.model.enums.ResultadoChecklist;
import com.deicmar.checklist.model.enums.Turno;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistResponse {
    
    private Long id;
    private String data;
    private String diaSemana;
    private String horaVistoria;
    private Turno turno;
    private Integer horimetroInicial;
    private Integer horimetroFinal;
    private UsuarioResponse operador;
    private EmpilhadeiraResponse empilhadeira;
    private ResultadoChecklist resultado;
    private List<ItemChecklistResponse> itens;
    private String observacaoGeral;
}
