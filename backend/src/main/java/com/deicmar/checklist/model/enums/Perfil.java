package com.deicmar.checklist.model.enums;

public enum Perfil {
    OPERADOR("Operador"),
    SUPERVISOR("Supervisor"),
    SUPERVISOR_OPERACIONAL("Supervisor Operacional"),
    GERENTE_MECANICA("Gerente de Mecânica"),
    ADMIN("Administrador");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
