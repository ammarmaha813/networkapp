package net.thevpc.samples.springnuts.moduleai.ws;

import net.thevpc.samples.springnuts.moduleai.model.ThreatResult;
import net.thevpc.samples.springnuts.moduleai.service.api.ILogIngestionService;  // ← MODIFICATION
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogsController {

    private final ILogIngestionService logIngestionService;  // ← MODIFICATION
    private final List<ThreatResult> anomalies = new ArrayList<>();

    public LogsController(ILogIngestionService logIngestionService) {  // ← MODIFICATION
        this.logIngestionService = logIngestionService;
    }

    @PostMapping
    public ResponseEntity<ThreatResult> ingestLog(@RequestBody String rawLog) {
        ThreatResult result = logIngestionService.ingest(rawLog);

        if (!result.getClassification().equals("NORMAL")) {
            anomalies.add(result);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/anomalies")
    public ResponseEntity<List<ThreatResult>> getAnomalies() {
        return ResponseEntity.ok(anomalies);
    }
}