package net.thevpc.samples.springnuts.moduleai.service.api;

import org.springframework.ai.document.Document;
import java.util.List;

/**
 * Interface publique pour le vector store
 */
public interface IVectorStoreService {
    List<Document> searchBySeverity(String query, String severity, int limit);
    List<Document> searchBySourceIp(String sourceIp, int limit);
    List<Document> searchByTimeRange(String query, String startTime, String endTime, int limit);
}
