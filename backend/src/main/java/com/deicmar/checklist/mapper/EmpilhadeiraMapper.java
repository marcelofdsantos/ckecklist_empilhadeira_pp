package com.deicmar.checklist.mapper;

import com.deicmar.checklist.dto.empilhadeira.EmpilhadeiraRequest;
import com.deicmar.checklist.dto.empilhadeira.EmpilhadeiraResponse;
import com.deicmar.checklist.model.entity.Empilhadeira;
import org.springframework.stereotype.Component;

@Component
public class EmpilhadeiraMapper {

    public EmpilhadeiraResponse toResponse(Empilhadeira empilhadeira) {
        if (empilhadeira == null) {
            return null;
        }
        
        return EmpilhadeiraResponse.builder()
                .id(empilhadeira.getId())
                .modelo(empilhadeira.getModelo())
                .tipo(empilhadeira.getTipo())
                .capacidade(empilhadeira.getCapacidade())
                .bloqueada(empilhadeira.getBloqueada())
                .motivoBloqueio(empilhadeira.getMotivoBloqueio())
                .ativa(empilhadeira.getAtiva())
                .build();
    }

    public Empilhadeira toEntity(EmpilhadeiraRequest request) {
        if (request == null) {
            return null;
        }
        
        return Empilhadeira.builder()
                .modelo(request.getModelo())
                .tipo(request.getTipo())
                .capacidade(request.getCapacidade())
                .bloqueada(false)
                .ativa(true)
                .build();
    }
}
