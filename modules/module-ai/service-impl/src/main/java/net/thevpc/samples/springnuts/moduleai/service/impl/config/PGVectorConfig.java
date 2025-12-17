package net.thevpc.samples.springnuts.moduleai.service.impl.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Configuration pour pgVector (base de données vectorielle)
 */
@Configuration
@ConditionalOnProperty(name = "spring.datasource.url")
public class PGVectorConfig {

    /**
     * Configuration des tables vectorielles
     */
    @Bean
    public void initializeVectorTables(JdbcTemplate jdbcTemplate) {
        // Créer l'extension pgVector si elle n'existe pas
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");

        // Créer la table pour les logs vectorisés
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS network_logs_vector (
                id VARCHAR(255) PRIMARY KEY,
                log_data JSONB,
                embedding vector(512),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Créer l'index vectoriel pour la recherche sémantique
        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS network_logs_vector_embedding_idx 
            ON network_logs_vector USING ivfflat (embedding vector_cosine_ops)
            WITH (lists = 100)
        """);
    }
}