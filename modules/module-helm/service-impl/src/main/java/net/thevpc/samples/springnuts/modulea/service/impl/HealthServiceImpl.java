package net.thevpc.samples.springnuts.helm.service.impl;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import net.thevpc.samples.springnuts.helm.service.api.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HealthServiceImpl implements HealthService {

    @Autowired
    private KubernetesClient kubernetesClient;

    @Override
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        try {
            // Check cluster connectivity
            String clusterVersion = kubernetesClient.getKubernetesVersion().getGitVersion();
            health.put("clusterVersion", clusterVersion);
            health.put("clusterStatus", "connected");

            // Count resources
            int namespaceCount = kubernetesClient.namespaces().list().getItems().size();
            int deploymentCount = kubernetesClient.apps().deployments().inAnyNamespace().list().getItems().size();
            int podCount = kubernetesClient.pods().inAnyNamespace().list().getItems().size();

            health.put("namespaceCount", namespaceCount);
            health.put("deploymentCount", deploymentCount);
            health.put("podCount", podCount);
            health.put("status", "healthy");

        } catch (Exception e) {
            health.put("status", "unhealthy");
            health.put("error", e.getMessage());
        }

        return health;
    }

    @Override
    public Map<String, Object> getDeploymentHealth(String namespace, String deploymentName) {
        Map<String, Object> health = new HashMap<>();

        try {
            io.fabric8.kubernetes.api.model.apps.Deployment deployment = kubernetesClient.apps().deployments()
                    .inNamespace(namespace)
                    .withName(deploymentName)
                    .get();

            if (deployment != null) {
                health.put("name", deployment.getMetadata().getName());
                health.put("namespace", deployment.getMetadata().getNamespace());
                health.put("replicas", deployment.getSpec().getReplicas());
                health.put("readyReplicas", deployment.getStatus().getReadyReplicas());
                health.put("availableReplicas", deployment.getStatus().getAvailableReplicas());

                // Check pod status
                List<Pod> pods = kubernetesClient.pods()
                        .inNamespace(namespace)
                        .withLabel("app", deploymentName)
                        .list()
                        .getItems();

                Map<String, Long> podStatusCount = pods.stream()
                        .collect(Collectors.groupingBy(
                                pod -> pod.getStatus().getPhase(),
                                Collectors.counting()
                        ));

                health.put("podStatus", podStatusCount);
                health.put("overallHealth", calculateOverallHealth(deployment, pods));
            } else {
                health.put("status", "not_found");
            }

        } catch (Exception e) {
            health.put("status", "error");
            health.put("error", e.getMessage());
        }

        return health;
    }

    private String calculateOverallHealth(io.fabric8.kubernetes.api.model.apps.Deployment deployment, List<Pod> pods) {
        int desiredReplicas = deployment.getSpec().getReplicas();
        int readyReplicas = deployment.getStatus().getReadyReplicas();

        if (readyReplicas == desiredReplicas) {
            long runningPods = pods.stream()
                    .filter(pod -> "Running".equals(pod.getStatus().getPhase()))
                    .count();

            if (runningPods == desiredReplicas) {
                return "healthy";
            } else {
                return "degraded";
            }
        } else if (readyReplicas > 0) {
            return "partial";
        } else {
            return "unhealthy";
        }
    }
}