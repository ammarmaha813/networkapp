package net.thevpc.samples.springnuts.helm.ws.rest;

import net.thevpc.samples.springnuts.helm.service.api.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/helm/health")
public class HealthController {

    @Autowired
    private HealthService healthService;

    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = healthService.getSystemHealth();
        return ResponseEntity.ok(health);
    }

    @GetMapping("/deployments/{namespace}/{deploymentName}")
    public ResponseEntity<Map<String, Object>> getDeploymentHealth(
            @PathVariable String namespace,
            @PathVariable String deploymentName) {
        Map<String, Object> health = healthService.getDeploymentHealth(namespace, deploymentName);
        return ResponseEntity.ok(health);
    }
}