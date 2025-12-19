package net.thevpc.samples.springnuts.helm.service.impl;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import net.thevpc.samples.springnuts.helm.model.DeploymentRequest;
import net.thevpc.samples.springnuts.helm.model.DeploymentStatus;
import net.thevpc.samples.springnuts.helm.service.api.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeploymentServiceImpl implements DeploymentService {

    @Autowired
    private KubernetesClient kubernetesClient;

    @Override
    public DeploymentStatus createDeployment(DeploymentRequest request) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(request.getName())
                .withNamespace(request.getNamespace())
                .addToLabels("app", request.getName())
                .addToLabels("module", "helm")
                .endMetadata()
                .withNewSpec()
                .withReplicas(request.getReplicas())
                .withNewSelector()
                .addToMatchLabels("app", request.getName())
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", request.getName())
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(request.getContainerName())
                .withImage(request.getImage())
                .addNewPort()
                .withContainerPort(request.getPort())
                .endPort()
                .withNewLivenessProbe()
                .withNewHttpGet()
                .withPath("/actuator/health/liveness")
                .withPort(io.fabric8.kubernetes.api.model.IntOrString.fromString("8080"))
                .endHttpGet()
                .withInitialDelaySeconds(30)
                .withPeriodSeconds(10)
                .endLivenessProbe()
                .withNewReadinessProbe()
                .withNewHttpGet()
                .withPath("/actuator/health/readiness")
                .withPort(io.fabric8.kubernetes.api.model.IntOrString.fromString("8080"))
                .endHttpGet()
                .withInitialDelaySeconds(30)
                .withPeriodSeconds(5)
                .endReadinessProbe()
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();

        Deployment created = kubernetesClient.apps().deployments()
                .inNamespace(request.getNamespace())
                .create(deployment);

        return convertToStatus(created);
    }

    @Override
    public DeploymentStatus getDeploymentStatus(String name, String namespace) {
        Deployment deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(name)
                .get();

        return deployment != null ? convertToStatus(deployment) : null;
    }

    @Override
    public DeploymentStatus scaleDeployment(String name, String namespace, int replicas) {
        Deployment deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(name)
                .scale(replicas);

        return convertToStatus(deployment);
    }

    @Override
    public boolean deleteDeployment(String name, String namespace) {
        return kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(name)
                .delete();
    }

    @Override
    public void rollbackDeployment(String name, String namespace, int revision) {
        kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(name)
                .rolling()
                .rollback();
    }

    private DeploymentStatus convertToStatus(Deployment deployment) {
        DeploymentStatus status = new DeploymentStatus();
        status.setName(deployment.getMetadata().getName());
        status.setNamespace(deployment.getMetadata().getNamespace());

        if (deployment.getStatus() != null) {
            status.setReplicas(deployment.getStatus().getReplicas() != null ? deployment.getStatus().getReplicas() : 0);
            status.setReadyReplicas(deployment.getStatus().getReadyReplicas() != null ? deployment.getStatus().getReadyReplicas() : 0);
            status.setAvailableReplicas(deployment.getStatus().getAvailableReplicas() != null ? deployment.getStatus().getAvailableReplicas() : 0);

            if (deployment.getStatus().getConditions() != null) {
                List<String> conditions = deployment.getStatus().getConditions().stream()
                        .map(condition -> condition.getType() + ": " + condition.getStatus())
                        .collect(Collectors.toList());
                status.setConditions(conditions);
            }
        }

        return status;
    }
}