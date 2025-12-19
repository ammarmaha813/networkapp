package net.thevpc.samples.springnuts.helm.ws.rest;

import net.thevpc.samples.springnuts.helm.model.HelmResponse;
import net.thevpc.samples.springnuts.helm.service.api.DeploymentService;
import net.thevpc.samples.springnuts.helm.service.api.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/helm")
public class HelmController {

    @Autowired
    private DeploymentService deploymentService;

    @Autowired
    private HealthService healthService;

    @GetMapping("/health")
    public Map<String, Object> getHealth() {
        return healthService.getSystemHealth();
    }

    @GetMapping("/deployments/{namespace}/{name}")
    public HelmResponse getDeploymentStatus(
            @PathVariable String namespace, 
            @PathVariable String name) {
        String status = deploymentService.getDeploymentStatus(name, namespace);
        return new HelmResponse("success", status);
    }

    @PostMapping("/deployments/{namespace}/{name}")
    public HelmResponse createDeployment(
            @PathVariable String namespace, 
            @PathVariable String name) {
        String result = deploymentService.createDeployment(name, namespace);
        return new HelmResponse("success", result);
    }

    @GetMapping("/test")
    public HelmResponse test() {
        String result = deploymentService.testConnection();
        return new HelmResponse("success", result);
    }

    @GetMapping("/info")
    public HelmResponse info() {
        HelmResponse response = new HelmResponse("success", "Module Helm is running");
        response.setData(java.util.Map.of(
            "version", "1.0.0",
            "module", "helm",
            "description", "Deployment and orchestration module"
        ));
        return response;
    }
}
