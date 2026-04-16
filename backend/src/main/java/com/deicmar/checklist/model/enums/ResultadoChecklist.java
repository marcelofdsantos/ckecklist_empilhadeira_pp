package com.deicmar.checklist.model.enums;

public enum ResultadoChecklist {
    APROVADO("Aprovado"),
    REPROVADO("Reprovado");

    private final String descricao;

    ResultadoChecklist(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
