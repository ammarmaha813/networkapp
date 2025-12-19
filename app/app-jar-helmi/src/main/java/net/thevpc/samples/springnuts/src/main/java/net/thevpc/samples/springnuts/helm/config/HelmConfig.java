package net.thevpc.samples.springnuts.helm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.thevpc.samples.springnuts.helm")
public class HelmConfig {
}
