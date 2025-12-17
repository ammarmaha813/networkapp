package net.thevpc.samples.springnuts.moduleai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatResult {

    private String logId;
    private LocalDateTime analyzedAt;
    private ThreatLevel threatLevel;
    private Double confidenceScore;
    private String classification;
    private String description;
    private List<String> indicators;
    private List<String> recommendations;
    private String aiReasoning;

    public enum ThreatLevel {
        NORMAL,
        SUSPICIOUS,
        ATTACK,
        CRITICAL
    }

    // Constructeur personnalisé avec NetworkLog - CORRECTION
    public ThreatResult(String classification, String analysis, double confidence, NetworkLog log) {
        this.logId           = log.getId(); // Cette ligne devrait fonctionner avec Lombok @Data
        this.analyzedAt      = LocalDateTime.now();
        this.threatLevel     = ThreatLevel.valueOf(classification);
        this.confidenceScore = confidence;
        this.classification  = classification;
        this.description     = analysis;
        this.indicators      = List.of();
        this.recommendations = List.of();
        this.aiReasoning     = analysis;
    }
}