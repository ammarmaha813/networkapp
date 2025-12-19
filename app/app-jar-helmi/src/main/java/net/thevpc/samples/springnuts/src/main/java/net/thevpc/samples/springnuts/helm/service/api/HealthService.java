package net.thevpc.samples.springnuts.helm.service.api;

import java.util.Map;

public interface HealthService {
    Map<String, Object> getSystemHealth();
}
