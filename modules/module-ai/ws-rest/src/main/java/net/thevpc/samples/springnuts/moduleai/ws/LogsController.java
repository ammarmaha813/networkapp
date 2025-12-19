package net.thevpc.samples.springnuts.moduleai.ws;

import net.thevpc.samples.springnuts.moduleai.model.ThreatResult;
import net.thevpc.samples.springnuts.moduleai.service.api.ILogIngestionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur corrigé pour l'ingestion de logs réseau
 */
@RestController
@RequestMapping("/logs")
@Slf4j
public class LogsController {

    private final ILogIngestionService logIngestionService;
    private final ObjectMapper objectMapper;
    private final List<ThreatResult> anomalies = new ArrayList<>();

    public LogsController(ILogIngestionService logIngestionService) {
        this.logIngestionService = logIngestionService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * POST /logs - Ingère un log réseau (méthode principale)
     * Accepte 3 formats:
     * 1. Texte brut: "2025-12-18T06:15:00 src=IP:PORT dst=IP:PORT proto=PROTO action=ACTION"
     * 2. JSON avec rawLog: {"rawLog": "texte du log"}
     * 3. JSON complet NetworkLog: {"timestamp": "...", "source_ip": "...", ...}
     */
    @PostMapping
    public ResponseEntity<?> ingestLogPost(@RequestBody String requestBody) {
        return processLogIngestion(requestBody, "POST");
    }

    /**
     * GET /logs - Liste des anomalies détectées
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLogsInfo() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "service", "Log Ingestion",
                "total_anomalies", anomalies.size(),
                "timestamp", java.time.LocalDateTime.now().toString(),
                "message", "Utilisez POST /logs pour ingérer un log, GET /logs/anomalies pour voir les anomalies"
        ));
    }

    /**
     * GET /logs/health - Vérification de santé du service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "Log Ingestion",
                "total_anomalies", anomalies.size(),
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    /**
     * GET /logs/anomalies - Récupère toutes les anomalies détectées
     */
    @GetMapping("/anomalies")
    public ResponseEntity<Map<String, Object>> getAnomalies() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "count", anomalies.size(),
                "anomalies", anomalies
        ));
    }

    /**
     * POST /logs/anomalies - Efface toutes les anomalies stockées
     */
    @PostMapping("/anomalies/clear")
    public ResponseEntity<Map<String, Object>> clearAnomalies() {
        int count = anomalies.size();
        anomalies.clear();
        log.info("Anomalies effacées: {} éléments supprimés", count);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Anomalies cleared",
                "deleted", count
        ));
    }

    /**
     * DELETE /logs/anomalies - Efface toutes les anomalies (alias)
     */
    @DeleteMapping("/anomalies")
    public ResponseEntity<Map<String, Object>> deleteAnomalies() {
        return clearAnomalies();
    }

    // Méthode privée pour traiter l'ingestion de log
    private ResponseEntity<?> processLogIngestion(String requestBody, String method) {
        try {
            String rawLog;

            // Détecter le format de la requête
            String trimmed = requestBody.trim();

            if (trimmed.startsWith("{")) {
                // Format JSON
                try {
                    JsonNode jsonNode = objectMapper.readTree(trimmed);

                    if (jsonNode.has("rawLog")) {
                        // Format: {"rawLog": "..."}
                        rawLog = jsonNode.get("rawLog").asText();
                        log.debug("Format détecté: JSON avec rawLog");
                    } else {
                        // Format: JSON complet du NetworkLog
                        rawLog = trimmed;
                        log.debug("Format détecté: JSON complet NetworkLog");
                    }
                } catch (Exception e) {
                    log.error("Erreur de parsing JSON", e);
                    return ResponseEntity.badRequest()
                            .body(Map.of(
                                    "status", "error",
                                    "message", "Invalid JSON format",
                                    "details", e.getMessage()
                            ));
                }
            } else {
                // Format texte brut
                rawLog = trimmed;
                log.debug("Format détecté: Texte brut");
            }

            // Ingérer le log
            ThreatResult result = logIngestionService.ingest(rawLog);

            // Stocker les anomalies
            if (!result.getClassification().equals("NORMAL")) {
                anomalies.add(result);
                log.info("Anomalie détectée: {} - Threat Level: {}",
                        result.getLogId(), result.getThreatLevel());
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Erreur lors de l'ingestion du log", e);
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage(),
                            "hint", "Supported formats: " +
                                    "1) Text: '2025-12-18T06:15:00 src=IP:PORT dst=IP:PORT proto=PROTO action=ACTION' " +
                                    "2) JSON wrapper: {\"rawLog\": \"...\"} " +
                                    "3) Full JSON: {\"timestamp\": \"...\", \"source_ip\": \"...\", ...}",
                            "method", method
                    ));
        }
    }
}