package net.thevpc.samples.springnuts.helm.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "deployment")
public class DeploymentConfig {
    private String namespace = "default";
    private String imageRegistry = "docker.io";
    private String imageRepository = "yourorg/network-security-platform";
    private String imageTag = "latest";
    private int replicas = 3;

    // Getters and setters
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public String getImageRegistry() { return imageRegistry; }
    public void setImageRegistry(String imageRegistry) { this.imageRegistry = imageRegistry; }
    public String getImageRepository() { return imageRepository; }
    public void setImageRepository(String imageRepository) { this.imageRepository = imageRepository; }
    public String getImageTag() { return imageTag; }
    public void setImageTag(String imageTag) { this.imageTag = imageTag; }
    public int getReplicas() { return replicas; }
    public void setReplicas(int replicas) { this.replicas = replicas; }
}