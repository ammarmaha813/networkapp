package net.thevpc.samples.springnuts.helm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.thevpc.samples.springnuts.helm")
public class ModuleHelmApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModuleHelmApplication.class, args);
    }
}
