package net.thevpc.samples.springnuts.moduleai.service.impl.service;

import net.thevpc.samples.springnuts.moduleai.model.NetworkLog;
import net.thevpc.samples.springnuts.moduleai.model.ThreatResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreatDetectionService {

    private final ChatModel chatModel;   // injection via Lombok

    public ThreatResult analyze(NetworkLog log) {
        String prompt = """
                You are a cybersecurity expert. Analyze this network log and classify it:
                
                %s
                
                Respond ONLY with one word: NORMAL, SUSPICIOUS, or ATTACK
                Then on a new line, explain briefly why.
                """.formatted(log.toEmbeddingText());

        String resp = chatModel.call(prompt);
        String[] lines = resp.split("\n", 2);
        String classification = lines[0].trim().toUpperCase();
        String analysis = lines.length > 1 ? lines[1].trim() : "No details";

        double confidence = classification.contains("NORMAL") ? 0.5 :
                classification.contains("SUSPICIOUS") ? 0.75 : 0.9;

        // constructeur simplifié que vous aviez au départ
        return new ThreatResult(classification, analysis, confidence, log);
    }
}