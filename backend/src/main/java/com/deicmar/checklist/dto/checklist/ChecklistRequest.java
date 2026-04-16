package com.deicmar.checklist.dto.checklist;

import com.deicmar.checklist.model.enums.Turno;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistRequest {

    @NotNull(message = "Data é obrigatória")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Data deve estar no formato YYYY-MM-DD")
    private String data;

    @NotNull(message = "Hora da vistoria é obrigatória")
    @Pattern(regexp = "\\d{2}:\\d{2}:\\d{2}", message = "Hora deve estar no formato HH:mm:ss")
    private String horaVistoria;

    @NotNull(message = "Turno é obrigatório")
    private Turno turno;

    @NotNull(message = "Horímetro inicial é obrigatório")
    @PositiveOrZero(message = "Horímetro inicial deve ser zero ou positivo")
    private Integer horimetroInicial;

    @PositiveOrZero(message = "Horímetro final deve ser zero ou positivo")
    private Integer horimetroFinal;

    @NotNull(message = "ID do operador é obrigatório")
    @Positive(message = "ID do operador deve ser positivo")
    private Long operadorId;

    @NotNull(message = "ID da empilhadeira é obrigatório")
    @Positive(message = "ID da empilhadeira deve ser positivo")
    private Long empilhadeiraId;

    @NotEmpty(message = "Lista de itens não pode estar vazia")
    @Valid
    private List<ItemChecklistRequest> itens;

    @Size(max = 1000, message = "Observação geral deve ter no máximo 1000 caracteres")
    private String observacaoGeral;
}
