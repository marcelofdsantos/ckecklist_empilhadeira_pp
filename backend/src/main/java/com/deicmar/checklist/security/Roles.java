package com.deicmar.checklist.security;

/**
 * Constantes de roles para usar nos @PreAuthorize.
 * Centraliza a lógica de quais perfis têm acesso a quê.
 */
public final class Roles {
    private Roles() {}

    // Perfis que podem GERENCIAR empilhadeiras (bloquear/desbloquear)
    public static final String GESTAO_EMPILHADEIRA =
        "hasAnyRole('ADMIN','GERENTE_MECANICA')";

    // Perfis que podem VER checklists e usuários (supervisão operacional)
    public static final String SUPERVISAO =
        "hasAnyRole('ADMIN','SUPERVISOR_OPERACIONAL','SUPERVISOR')";

    // Perfis que têm acesso ao painel (qualquer nível gerencial)
    public static final String PAINEL =
        "hasAnyRole('ADMIN','GERENTE_MECANICA','SUPERVISOR_OPERACIONAL','SUPERVISOR')";

    // Só o admin master
    public static final String ADMIN_ONLY =
        "hasRole('ADMIN')";
}
