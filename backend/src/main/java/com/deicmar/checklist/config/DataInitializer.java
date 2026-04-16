package com.deicmar.checklist.config;

import com.deicmar.checklist.model.entity.Empilhadeira;
import com.deicmar.checklist.model.entity.Usuario;
import com.deicmar.checklist.model.enums.Perfil;
import com.deicmar.checklist.repository.EmpilhadeiraRepository;
import com.deicmar.checklist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final EmpilhadeiraRepository empilhadeiraRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.admin.senha:admin123}")
    private String adminSenha;

    @Value("${seed.default.senha:senha123}")
    private String defaultSenha;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("Inicializando dados do sistema...");
            criarUsuarios();
            criarEmpilhadeiras();
            log.info("Dados inicializados com sucesso.");
        };
    }

    private void criarUsuarios() {
        criarUsuario("ADMIN",  "Administrador do Sistema", adminSenha,   Perfil.ADMIN);
        criarUsuario("GERENTE","Gerente de Mecânica",      defaultSenha, Perfil.GERENTE_MECANICA);
        criarUsuario("SUP001", "Supervisor Operacional",   defaultSenha, Perfil.SUPERVISOR_OPERACIONAL);
        criarUsuario("SUP002", "Supervisor Turno B",       defaultSenha, Perfil.SUPERVISOR_OPERACIONAL);
        criarUsuario("SUP999", "Supervisor Geral",         defaultSenha, Perfil.SUPERVISOR);
        criarUsuario("OPR001", "João da Silva",            defaultSenha, Perfil.OPERADOR);
        criarUsuario("OPR002", "Maria Santos",             defaultSenha, Perfil.OPERADOR);
        criarUsuario("OPR003", "Pedro Oliveira",           defaultSenha, Perfil.OPERADOR);
        criarUsuario("313682", "Marcelo Santos",           defaultSenha, Perfil.OPERADOR);
    }

    private void criarUsuario(String re, String nome, String senha, Perfil perfil) {
        if (usuarioRepository.existsByRe(re)) {
            log.debug("Usuário já existe, ignorando — RE: {}", re);
            return;
        }
        usuarioRepository.save(Usuario.builder()
                .re(re).nomeCompleto(nome)
                .senha(passwordEncoder.encode(senha))
                .perfil(perfil).ativo(true).build());
        log.info("Usuário criado — RE: {}, Perfil: {}", re, perfil);
    }

    private void criarEmpilhadeiras() {
        if (empilhadeiraRepository.count() > 0) return;
        criarEmpilhadeira("YALE GP030",        "Elétrica", 3000, false, null);
        criarEmpilhadeira("TOYOTA/FROTA 224",  "Elétrica", 2000, false, null);
        criarEmpilhadeira("HYSTER H50FT",      "GLP",      5000, false, null);
        criarEmpilhadeira("CATERPILLAR GP25N", "Diesel",   2500, false, null);
        criarEmpilhadeira("LINDE E16",         "Elétrica", 1600, true,  "Manutenção preventiva");
        criarEmpilhadeira("STILL RX60-30",     "Elétrica", 3000, false, null);
        log.info("6 empilhadeiras criadas.");
    }

    private void criarEmpilhadeira(String modelo, String tipo, int cap, boolean bloqueada, String motivo) {
        empilhadeiraRepository.save(Empilhadeira.builder()
                .modelo(modelo).tipo(tipo).capacidade(cap)
                .bloqueada(bloqueada).motivoBloqueio(motivo).ativa(true).build());
    }
}
