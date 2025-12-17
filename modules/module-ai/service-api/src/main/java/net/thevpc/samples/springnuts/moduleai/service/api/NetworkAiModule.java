package net.thevpc.samples.springnuts.moduleai.service.api;

import net.thevpc.samples.springnuts.moduleai.model.ThreatResult;

import java.util.List;

public interface NetworkAiModule {

    /**
     * Ingère et analyse un log réseau brut
     */
    ThreatResult ingestLog(String rawLog);

    /**
     * Pose une question sur les logs réseau via RAG
     */
    String askQuestion(String question);

    /**
     * Récupère toutes les anomalies détectées
     */
    List<ThreatResult> getAnomalies();

    /**
     * Récupère les anomalies pour une IP source spécifique
     */
    List<ThreatResult> getAnomaliesByIp(String sourceIp);
}