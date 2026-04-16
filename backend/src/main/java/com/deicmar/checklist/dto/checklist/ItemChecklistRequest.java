package com.deicmar.checklist.dto.checklist;

import com.deicmar.checklist.model.enums.StatusItem;
import com.deicmar.checklist.model.enums.TipoItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemChecklistRequest {

    @NotBlank(message = "Descrição do item é obrigatória")
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    private String descricao;

    @NotNull(message = "Tipo do item é obrigatório")
    private TipoItem tipo;

    @NotNull(message = "Status do item é obrigatório")
    private StatusItem status;

    @Size(max = 500, message = "Observação deve ter no máximo 500 caracteres")
    private String observacao;
}
