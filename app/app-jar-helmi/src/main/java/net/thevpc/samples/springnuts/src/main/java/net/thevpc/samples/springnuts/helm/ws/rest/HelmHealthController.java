package net.thevpc.samples.springnuts.helm.ws.rest;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/helm/health")
public class HelmHealthController {

    @GetMapping("/status")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("module", "helm-health");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        return response;
    }

    @GetMapping("/ready")
    public Map<String, Object> ready() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ready");
        response.put("message", "Helm Health Controller is ready");
        return response;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("ping", "pong");
        response.put("controller", "HelmHealthController");
        return response;
    }
}
