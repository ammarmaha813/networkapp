package net.thevpc.samples.springnuts.moduleai.ws;

import net.thevpc.samples.springnuts.moduleai.service.impl.service.VectorStoreService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/anomalies")
@RequiredArgsConstructor
@Slf4j
public class AnomalyController {

    private final VectorStoreService vectorStoreService;

    /**
     * GET /api/anomalies - Récupérer les anomalies détectées
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAnomalies(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "CRITICAL,ATTACK") String severities) {
        try {
            List<String> severityList = List.of(severities.split(","));

            List<Document> anomalies = severityList.stream()
                    .flatMap(severity ->
                            vectorStoreService.searchBySeverity("anomaly threat attack", severity.trim(), limit)
                                    .stream())
                    .distinct()
                    .limit(limit)
                    .toList();

            List<Map<String, Object>> results = anomalies.stream()
                    .map(doc -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("log_id", doc.getMetadata().get("log_id"));
                        map.put("timestamp", doc.getMetadata().get("timestamp"));
                        map.put("source_ip", doc.getMetadata().get("source_ip"));
                        map.put("destination_ip", doc.getMetadata().get("destination_ip"));
                        map.put("severity", doc.getMetadata().get("severity"));
                        map.put("content", doc.getText());
                        return map;
                    })
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", results.size(),
                    "anomalies", results));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des anomalies", e);
            return ResponseEntity.status(500)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * GET /api/anomalies/by-ip - Anomalies par IP
     */
    @GetMapping("/by-ip/{sourceIp}")
    public ResponseEntity<Map<String, Object>> getAnomaliesByIp(
            @PathVariable String sourceIp,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Document> logs = vectorStoreService.searchBySourceIp(sourceIp, limit);

            List<Map<String, Object>> results = logs.stream()
                    .map(doc -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("log_id", doc.getMetadata().get("log_id"));
                        map.put("timestamp", doc.getMetadata().get("timestamp"));
                        map.put("destination_ip", doc.getMetadata().get("destination_ip"));
                        map.put("action", doc.getMetadata().get("action"));
                        map.put("severity", doc.getMetadata().get("severity"));
                        map.put("content", doc.getText());
                        return map;
                    })
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "source_ip", sourceIp,
                    "count", results.size(),
                    "logs", results));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des logs par IP", e);
            return ResponseEntity.status(500)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * POST /api/anomalies/search-timerange - Recherche par plage temporelle
     */
    @PostMapping("/search-timerange")
    public ResponseEntity<Map<String, Object>> searchByTimeRange(@RequestBody TimeRangeRequest request) {
        try {
            List<Document> logs = vectorStoreService.searchByTimeRange(
                    request.getQuery(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getLimit());

            List<Map<String, Object>> results = logs.stream()
                    .map(doc -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("log_id", doc.getMetadata().get("log_id"));
                        map.put("timestamp", doc.getMetadata().get("timestamp"));
                        map.put("source_ip", doc.getMetadata().get("source_ip"));
                        map.put("destination_ip", doc.getMetadata().get("destination_ip"));
                        map.put("severity", doc.getMetadata().get("severity"));
                        map.put("content", doc.getText());
                        return map;
                    })
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "time_range", Map.of("start", request.getStartTime(), "end", request.getEndTime()),
                    "count", results.size(),
                    "logs", results));
        } catch (Exception e) {
            log.error("Erreur lors de la recherche temporelle", e);
            return ResponseEntity.status(500)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Data
    public static class TimeRangeRequest {
        private String query;
        private String startTime;
        private String endTime;
        private int limit = 10;
    }
}