package net.thevpc.samples.springnuts.moduleai.service.impl.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.datasource.url")
public class PGVectorConfig {

    private final JdbcTemplate jdbcTemplate;

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(768)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("vector_store")
                .maxDocumentBatchSize(10000)
                .build();
    }

    @PostConstruct
    public void initializeCustomVectorTables() {
        try {
            log.info("Initialisation des tables vectorielles personnalisées...");

            // Créer l'extension pgVector (déjà fait par Spring AI)
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");

            // Tables personnalisées si nécessaire
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS network_logs_vector (
                    id VARCHAR(255) PRIMARY KEY,
                    log_data JSONB,
                    embedding vector(768),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Index personnalisé
            jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS network_logs_vector_embedding_idx 
                ON network_logs_vector USING ivfflat (embedding vector_cosine_ops)
                WITH (lists = 100)
            """);

            log.info("✓ Tables vectorielles personnalisées initialisées");
        } catch (Exception e) {
            log.warn("Erreur lors de l'initialisation des tables personnalisées (peut être ignoré si Spring AI les gère): {}", e.getMessage());
        }
    }
}