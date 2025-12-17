package net.thevpc.samples.springnuts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan(basePackages = {
        "net.thevpc.samples.springnuts.moduleai",
        "net.thevpc.samples.springnuts.core"
})
@Slf4j
public class SpringNutsApplication {

    public static void main(String[] args) {
        System.setProperty("spring.application.name", "module-ai-rag");
        System.setProperty("server.port", "8082");
        SpringApplication.run(SpringNutsApplication.class, args);
    }

    @Bean
    public CommandLineRunner init() {
        return args -> {
            log.info("============================================================");
            log.info("🤖 Module AI - RAG Network Security Analysis System");
            log.info("============================================================");
            log.info("");
            log.info("🔧 Module AI Services Initialized:");
            log.info("   ✅ Log Ingestion Service (Batch processing)");
            log.info("   ✅ Vector Store Service (pgvector + HNSW)");
            log.info("   ✅ Threat Detection Service (LLM-powered)");
            log.info("   ✅ RAG Service (Ollama + embeddings)");
            log.info("   ✅ Embedding Service (nomic-embed-text)");
            log.info("   ✅ Network Log Parser");
            log.info("");
            log.info("🌐 REST API Endpoints:");
            log.info("   📊 GET  http://localhost:8082/api/anomalies");
            log.info("   💬 POST http://localhost:8082/ask");
            log.info("   📝 POST http://localhost:8082/logs");
            log.info("   🔍 GET  http://localhost:8082/api/anomalies/by-ip/{ip}");
            log.info("   ⏰ POST http://localhost:8082/api/anomalies/search-timerange");
            log.info("   🩺 GET  http://localhost:8082/actuator/health");
            log.info("");
            log.info("🔗 Infrastructure:");
            log.info("   🗄️  Database: PostgreSQL + pgvector");
            log.info("   🤖 AI: Ollama (llama3.2 + nomic-embed-text)");
            log.info("   🌍 HTTP: Port 8082");
            log.info("");
            log.info("🎯 Module AI RAG System Ready!");
            log.info("============================================================");
        };
    }
}