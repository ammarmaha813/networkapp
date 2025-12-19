package net.thevpc.samples.springnuts.helm.ws.rest;

import net.thevpc.samples.springnuts.helm.model.DeploymentRequest;
import net.thevpc.samples.springnuts.helm.model.DeploymentStatus;
import net.thevpc.samples.springnuts.helm.service.api.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/helm/deployments")
public class DeploymentController {

    @Autowired
    private DeploymentService deploymentService;

    @PostMapping
    public ResponseEntity<DeploymentStatus> createDeployment(@RequestBody DeploymentRequest request) {
        DeploymentStatus status = deploymentService.createDeployment(request);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{namespace}/{name}")
    public ResponseEntity<DeploymentStatus> getDeploymentStatus(
            @PathVariable String namespace,
            @PathVariable String name) {
        DeploymentStatus status = deploymentService.getDeploymentStatus(name, namespace);
        return status != null ? ResponseEntity.ok(status) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{namespace}/{name}/scale/{replicas}")
    public ResponseEntity<DeploymentStatus> scaleDeployment(
            @PathVariable String namespace,
            @PathVariable String name,
            @PathVariable int replicas) {
        DeploymentStatus status = deploymentService.scaleDeployment(name, namespace, replicas);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/{namespace}/{name}")
    public ResponseEntity<Void> deleteDeployment(
            @PathVariable String namespace,
            @PathVariable String name) {
        boolean deleted = deploymentService.deleteDeployment(name, namespace);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{namespace}/{name}/rollback/{revision}")
    public ResponseEntity<Void> rollbackDeployment(
            @PathVariable String namespace,
            @PathVariable String name,
            @PathVariable int revision) {
        deploymentService.rollbackDeployment(name, namespace, revision);
        return ResponseEntity.ok().build();
    }
}