package com.deicmar.checklist.mapper;

import com.deicmar.checklist.dto.checklist.ChecklistResponse;
import com.deicmar.checklist.dto.checklist.ItemChecklistResponse;
import com.deicmar.checklist.model.entity.Checklist;
import com.deicmar.checklist.model.entity.ItemChecklist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChecklistMapper {

    private final UsuarioMapper usuarioMapper;
    private final EmpilhadeiraMapper empilhadeiraMapper;

    public ChecklistResponse toResponse(Checklist checklist) {
        if (checklist == null) {
            return null;
        }
        
        return ChecklistResponse.builder()
                .id(checklist.getId())
                .data(checklist.getData().toString())
                .diaSemana(checklist.getDiaSemana())
                .horaVistoria(checklist.getHoraVistoria().toString())
                .turno(checklist.getTurno())
                .horimetroInicial(checklist.getHorimetroInicial())
                .horimetroFinal(checklist.getHorimetroFinal())
                .operador(usuarioMapper.toResponse(checklist.getOperador()))
                .empilhadeira(empilhadeiraMapper.toResponse(checklist.getEmpilhadeira()))
                .resultado(checklist.getResultado())
                .itens(toItemResponseList(checklist.getItens()))
                .observacaoGeral(checklist.getObservacaoGeral())
                .build();
    }

    public ItemChecklistResponse toItemResponse(ItemChecklist item) {
        if (item == null) {
            return null;
        }
        
        return ItemChecklistResponse.builder()
                .id(item.getId())
                .descricao(item.getDescricao())
                .tipo(item.getTipo())
                .status(item.getStatus())
                .observacao(item.getObservacao())
                .build();
    }

    public List<ItemChecklistResponse> toItemResponseList(List<ItemChecklist> itens) {
        if (itens == null) {
            return null;
        }
        
        return itens.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }
}
