package net.thevpc.samples.springnuts.moduleai.service.impl;

import net.thevpc.samples.springnuts.moduleai.model.ThreatResult;
import net.thevpc.samples.springnuts.moduleai.service.api.NetworkAiModule;
import net.thevpc.samples.springnuts.moduleai.service.impl.service.LogIngestionService;
import net.thevpc.samples.springnuts.moduleai.service.impl.service.RAGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkAiModuleImpl implements NetworkAiModule {

    private final LogIngestionService logIngestionService;
    private final RAGService ragService;

    // Stockage en mémoire des anomalies (à remplacer par une vraie base de données)
    private final List<ThreatResult> anomalies = new ArrayList<>();

    @Override
    public ThreatResult ingestLog(String rawLog) {
        log.info("Ingestion d'un nouveau log réseau");
        ThreatResult result = logIngestionService.ingest(rawLog);

        // Stocker l'anomalie si ce n'est pas normal
        if (!result.getClassification().equals("NORMAL")) {
            anomalies.add(result);
            log.warn("Anomalie détectée: {} - {}", result.getClassification(), result.getLogId());
        }

        return result;
    }

    @Override
    public String askQuestion(String question) {
        log.info("Question posée: {}", question);
        return ragService.ask(question);
    }

    @Override
    public List<ThreatResult> getAnomalies() {
        log.info("Récupération de {} anomalies", anomalies.size());
        return new ArrayList<>(anomalies);
    }

    @Override
    public List<ThreatResult> getAnomaliesByIp(String sourceIp) {
        log.info("Recherche des anomalies pour l'IP: {}", sourceIp);
        return anomalies.stream()
                .filter(result -> {
                    // Filtrage basique - à améliorer avec les métadonnées
                    String description = result.getDescription();
                    return description != null && description.contains(sourceIp);
                })
                .toList();
    }
}