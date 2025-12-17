package net.thevpc.samples.springnuts;

import net.thevpc.nuts.app.NAppDefinition;
import net.thevpc.nuts.app.NAppRunner;
import net.thevpc.nuts.core.NWorkspace;
import net.thevpc.nuts.io.NPrintStream;
import net.thevpc.nuts.text.NMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@NAppDefinition
@SpringBootApplication
@ComponentScan(basePackages = {
        "net.thevpc.samples.springnuts.moduleai",
        "net.thevpc.samples.springnuts.moduleai.service.impl",
        "net.thevpc.samples.springnuts.moduleai.ws",
        "net.thevpc.samples.springnuts.moduleai.config",
        "net.thevpc.samples.springnuts.moduleai.dal",
        "net.thevpc.samples.springnuts.moduleai.utils",
        "net.thevpc.samples.springnuts.core",
        "net.thevpc.samples.springnuts.core.service.impl"
})
public class SpringNutsApplication {
    @Autowired
    private NWorkspace workspace;
    @Autowired
    private NPrintStream out;

    public static void main(String[] args) {
        System.setProperty("spring.application.name", "module-ai-rag");
        System.setProperty("server.port", "8082");
        SpringApplication.run(SpringNutsApplication.class, args);
    }

    @NAppRunner
    public void run() {
        out.println("🤖 Module AI - RAG Network Security Analysis System");
        out.println("================================================================");
        out.println(NMsg.ofC("🧠 Powered by Nuts Framework %s",
                workspace.getRuntimeId().getVersion()));
        out.println(NMsg.ofC("💻 Platform: %s | OS: %s | Arch: %s",
                workspace.getPlatform(),
                workspace.getOs(),
                workspace.getArch()));
        out.println();
        out.println("🔧 Module AI Services Initialized:");
        out.println("   ✅ Log Ingestion Service (Batch processing)");
        out.println("   ✅ Vector Store Service (pgvector + HNSW)");
        out.println("   ✅ Threat Detection Service (LLM-powered)");
        out.println("   ✅ RAG Service (Ollama + embeddings)");
        out.println("   ✅ Embedding Service (nomic-embed-text)");
        out.println("   ✅ Network Log Parser");
        out.println();
        out.println("🌐 REST API Endpoints:");
        out.println("   📊 GET  /api/anomalies - Detect anomalies");
        out.println("   💬 POST /ask - RAG-based Q&A");
        out.println("   📝 POST /logs - Ingest network logs");
        out.println("   🔍 GET  /api/anomalies/by-ip/{ip} - IP-based search");
        out.println("   ⏰ POST /api/anomalies/search-timerange - Time search");
        out.println("   🩺 GET  /actuator/health - Health check");
        out.println();
        out.println("🔗 Infrastructure:");
        out.println("   🗄️  Database: PostgreSQL + pgvector");
        out.println("   🤖 AI: Ollama (llama3.2 + nomic-embed-text)");
        out.println("   🌍 HTTP: Port 8082");
        out.println();
        out.println("🎯 Module AI RAG System Ready!");
    }
}