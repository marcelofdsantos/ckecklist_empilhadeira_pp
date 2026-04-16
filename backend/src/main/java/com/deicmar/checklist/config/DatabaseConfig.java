package com.deicmar.checklist.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Converte automaticamente a DATABASE_URL do formato Railway (postgres://)
 * para o formato JDBC (jdbc:postgresql://) que o Spring/Hikari entende.
 *
 * Railway injeta:  postgres://user:pass@host:port/db
 * Spring precisa:  jdbc:postgresql://host:port/db  (com user/pass separados)
 */
@Configuration
@Profile("prod")
@Slf4j
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "DATABASE_URL")
    public DataSource railwayDataSource() {
        String jdbcUrl = convertToJdbc(databaseUrl);
        String[] credentials = extractCredentials(databaseUrl);

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        if (credentials.length == 2) {
            ds.setUsername(credentials[0]);
            ds.setPassword(credentials[1]);
        }
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setMaximumPoolSize(3);
        ds.setMinimumIdle(1);
        ds.setConnectionTimeout(30_000);
        ds.setIdleTimeout(300_000);
        ds.setMaxLifetime(900_000);
        ds.setPoolName("RailwayPool");

        log.info("DataSource configurado para Railway. URL: {}", maskUrl(jdbcUrl));
        return ds;
    }

    /**
     * postgres://user:pass@host:port/db  →  jdbc:postgresql://host:port/db
     * jdbc:postgresql://...              →  mantém como está
     */
    private String convertToJdbc(String url) {
        if (url == null || url.isBlank()) return url;
        if (url.startsWith("jdbc:")) return url;
        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            // Remove scheme e credenciais: postgres://user:pass@host:port/db
            String withoutScheme = url.replaceFirst("^postgres(ql)?://", "");
            // withoutScheme = user:pass@host:port/db
            String hostPart = withoutScheme.contains("@")
                    ? withoutScheme.substring(withoutScheme.indexOf("@") + 1)
                    : withoutScheme;
            return "jdbc:postgresql://" + hostPart;
        }
        return url;
    }

    /** Extrai [username, password] da URL postgres://user:pass@host/db */
    private String[] extractCredentials(String url) {
        if (url == null || !url.contains("@")) return new String[0];
        String withoutScheme = url.replaceFirst("^postgres(ql)?://", "");
        String credentials = withoutScheme.substring(0, withoutScheme.indexOf("@"));
        return credentials.contains(":") ? credentials.split(":", 2) : new String[0];
    }

    /** Mascara a senha para log: jdbc:postgresql://host:port/db */
    private String maskUrl(String url) {
        return url != null ? url.replaceAll(":[^@/]+@", ":***@") : "";
    }
}
