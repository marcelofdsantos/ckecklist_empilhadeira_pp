package com.deicmar.checklist.model.enums;

public enum StatusItem {
    OK("Conforme"),
    NAO_CONFORME("Não conforme");

    private final String descricao;

    StatusItem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
