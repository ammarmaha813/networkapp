package net.thevpc.samples.springnuts.helm.service.api;

import net.thevpc.samples.springnuts.helm.model.DeploymentRequest;
import net.thevpc.samples.springnuts.helm.model.DeploymentStatus;

public interface DeploymentService {
    DeploymentStatus createDeployment(DeploymentRequest request);
    DeploymentStatus getDeploymentStatus(String name, String namespace);
    DeploymentStatus scaleDeployment(String name, String namespace, int replicas);
    boolean deleteDeployment(String name, String namespace);
    void rollbackDeployment(String name, String namespace, int revision);
}