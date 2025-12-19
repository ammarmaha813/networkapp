package net.thevpc.samples.springnuts.moduleai.ws;

import net.thevpc.samples.springnuts.moduleai.service.api.IRAGService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur corrigé pour les questions en langage naturel
 */
@RestController
@RequestMapping("/ask")
public class ChatController {

    private final IRAGService ragService;

    public ChatController(IRAGService ragService) {
        this.ragService = ragService;
    }

    /**
     * POST /ask - Pose une question (méthode principale)
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> askPost(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le paramètre 'question' est requis"));
        }

        String answer = ragService.ask(question);
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    /**
     * GET /ask - Pose une question via paramètres URL (pour compatibilité)
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> askGet(@RequestParam String question) {
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le paramètre 'question' est requis"));
        }

        String answer = ragService.ask(question);
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    /**
     * GET /ask/health - Vérification de santé du service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "RAG Chat",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}