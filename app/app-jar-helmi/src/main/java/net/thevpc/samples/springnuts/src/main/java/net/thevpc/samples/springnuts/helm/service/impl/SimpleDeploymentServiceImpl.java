package net.thevpc.samples.springnuts.helm.service.impl;

import net.thevpc.samples.springnuts.helm.service.api.DeploymentService;
import org.springframework.stereotype.Service;

@Service
public class SimpleDeploymentServiceImpl implements DeploymentService {

    @Override
    public String createDeployment(String name, String namespace) {
        return "Deployment '" + name + "' created successfully in namespace '" + namespace + "'";
    }

    @Override
    public String getDeploymentStatus(String name, String namespace) {
        return "Deployment '" + name + "' in namespace '" + namespace + "' is RUNNING";
    }

    @Override
    public String testConnection() {
        return "Helm module connection test successful - " + java.time.LocalDateTime.now();
    }
}
