package net.thevpc.samples.springnuts.moduleai.ws;

import net.thevpc.samples.springnuts.moduleai.service.impl.service.RAGService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ask")
public class ChatController {

    private final RAGService ragService;

    public ChatController(RAGService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> ask(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = ragService.ask(question);
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}