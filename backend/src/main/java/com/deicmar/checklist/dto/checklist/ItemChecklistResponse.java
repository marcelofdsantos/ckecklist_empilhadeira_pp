package com.deicmar.checklist.dto.checklist;

import com.deicmar.checklist.model.enums.StatusItem;
import com.deicmar.checklist.model.enums.TipoItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemChecklistResponse {
    
    private Long id;
    private String descricao;
    private TipoItem tipo;
    private StatusItem status;
    private String observacao;
}
