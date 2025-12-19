package net.thevpc.samples.springnuts.helm.service.api;

public interface DeploymentService {
    String createDeployment(String name, String namespace);
    String getDeploymentStatus(String name, String namespace);
    String testConnection();
}
