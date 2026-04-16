package com.deicmar.checklist.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Valida e exibe informações úteis no startup para facilitar diagnóstico.
 */
@Component
@Slf4j
public class StartupValidator {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${server.port:8080}")
    private String port;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${springdoc.swagger-ui.enabled:false}")
    private boolean swaggerEnabled;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("╔══════════════════════════════════════════════╗");
        log.info("║   Sistema de Checklist — Iniciado com sucesso ║");
        log.info("╠══════════════════════════════════════════════╣");
        log.info("║  Profile  : {}", activeProfile);
        log.info("║  Porta    : {}", port);
        log.info("║  Banco    : {}", maskUrl(datasourceUrl));
        log.info("║  API Base : http://localhost:{}/api", port);
        if (swaggerEnabled) {
            log.info("║  Swagger  : http://localhost:{}/api/swagger-ui.html", port);
        }
        log.info("╚══════════════════════════════════════════════╝");
    }

    private String maskUrl(String url) {
        if (url == null) return "não configurado";
        return url.replaceAll(":[^@/:]+@", ":***@")
                  .replaceAll("password=[^&]+", "password=***");
    }
}
