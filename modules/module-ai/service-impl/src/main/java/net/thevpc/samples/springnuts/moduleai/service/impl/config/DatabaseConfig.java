package net.thevpc.samples.springnuts.moduleai.service.impl.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseConfig {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializePGVector() {
        try {
            log.info("Initialisation de l'extension pgvector...");

            // Créer l'extension pgvector si elle n'existe pas
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");

            log.info("✓ Extension pgvector initialisée avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation de pgvector", e);
            throw new RuntimeException("Impossible d'initialiser pgvector. Assurez-vous que PostgreSQL avec pgvector est démarré.", e);
        }
    }
}