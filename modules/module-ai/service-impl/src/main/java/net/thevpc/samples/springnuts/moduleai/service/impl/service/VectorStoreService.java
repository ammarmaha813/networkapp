package net.thevpc.samples.springnuts.moduleai.service.impl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.thevpc.samples.springnuts.moduleai.model.NetworkLog;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import net.thevpc.samples.springnuts.moduleai.service.api.IVectorStoreService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorStoreService implements IVectorStoreService {

    private final VectorStore vectorStore;

    public void storeLog(NetworkLog networkLog) {
        // Générer un ID si absent
        String id = networkLog.getId();
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
            networkLog.setId(id);
        }

        // Construire les métadonnées en filtrant les valeurs null
        Map<String, Object> metadata = new HashMap<>();
        if (networkLog.getTimestamp() != null) metadata.put("timestamp", networkLog.getTimestamp().toString());
        if (networkLog.getSourceIp() != null) metadata.put("source_ip", networkLog.getSourceIp());
        if (networkLog.getDestinationIp() != null) metadata.put("destination_ip", networkLog.getDestinationIp());
        if (networkLog.getProtocol() != null) metadata.put("protocol", networkLog.getProtocol());
        if (networkLog.getSeverity() != null) metadata.put("severity", networkLog.getSeverity());
        if (networkLog.getAction() != null) metadata.put("action", networkLog.getAction());
        metadata.put("log_id", id); // toujours présent

        // Créer le document avec la méthode text()
        Document doc = Document.builder()
                .id(id)
                .text(networkLog.toEmbeddingText()) // ← ici, pas content(), mais text()
                .metadata(metadata)
                .build();

        vectorStore.add(List.of(doc));
        log.info("Log {} stocké dans PGVector", id);
    }

    /* --------------------- Recherches --------------------- */

    public List<Document> searchSimilarLogs(String query, int topK) {
        SearchRequest req = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build(); // PAS de similarityThreshold
        return vectorStore.similaritySearch(req);
    }

    public List<Document> searchBySeverity(String query, String severity, int topK) {
        return searchSimilarLogs(query, topK * 2).stream()
                .filter(d -> severity.equals(d.getMetadata().get("severity")))
                .limit(topK)
                .toList();
    }

    public List<Document> searchBySourceIp(String sourceIp, int topK) {
        return searchSimilarLogs("logs from " + sourceIp, topK * 2).stream()
                .filter(d -> sourceIp.equals(d.getMetadata().get("source_ip")))
                .limit(topK)
                .toList();
    }

    public List<Document> searchByTimeRange(String query, String startTime, String endTime, int topK) {
        return searchSimilarLogs(query, topK * 2).stream()
                .filter(d -> {
                    String ts = (String) d.getMetadata().get("timestamp");
                    return ts != null && ts.compareTo(startTime) >= 0 && ts.compareTo(endTime) <= 0;
                })
                .limit(topK)
                .toList();
    }

    // surcharge pour RAGService avec threshold
    public List<Document> searchSimilarLogs(String query, int topK, double threshold) {
        SearchRequest req = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(threshold)
                .build();
        return vectorStore.similaritySearch(req);
    }
}
