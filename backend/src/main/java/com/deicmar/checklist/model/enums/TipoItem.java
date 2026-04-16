package com.deicmar.checklist.model.enums;

public enum TipoItem {
    CONFORME("Item de conformidade"),
    IMPEDITIVO("Item impeditivo");

    private final String descricao;

    TipoItem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
