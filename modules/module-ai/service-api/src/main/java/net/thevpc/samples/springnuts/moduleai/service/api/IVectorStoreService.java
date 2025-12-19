package net.thevpc.samples.springnuts.moduleai.service.api;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * Service de gestion du stockage vectoriel (pgVector)
 */
public interface IVectorStoreService {

    /**
     * Recherche les logs similaires à une requête
     */
    List<Document> searchSimilarLogs(String query, int topK);

    /**
     * Recherche par sévérité
     */
    List<Document> searchBySeverity(String query, String severity, int topK);

    /**
     * Recherche par IP source
     */
    List<Document> searchBySourceIp(String sourceIp, int topK);

    /**
     * Recherche par plage temporelle
     */
    List<Document> searchByTimeRange(String query, String startTime, String endTime, int topK);

    /**
     * Recherche avec seuil de similarité
     */
    List<Document> searchSimilarLogs(String query, int topK, double threshold);
}