package com.deicmar.checklist.model.enums;

public enum Turno {
    A("Turno A - Manhã"),
    B("Turno B - Tarde"),
    C("Turno C - Noite");

    private final String descricao;

    Turno(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
