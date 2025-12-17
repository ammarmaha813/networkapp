package net.thevpc.samples.springnuts.moduleai.ws;

import net.thevpc.samples.springnuts.moduleai.service.api.IRAGService;  // ← MODIFICATION
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ask")
public class ChatController {

    private final IRAGService ragService;  // ← MODIFICATION

    public ChatController(IRAGService ragService) {  // ← MODIFICATION
        this.ragService = ragService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> ask(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = ragService.ask(question);
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}