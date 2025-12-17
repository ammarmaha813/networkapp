package net.thevpc.samples.springnuts.service.restcli;

import net.thevpc.samples.springnuts.moduleai.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HrModuleRestCli implements HrModule {
    @Autowired
    private RestTemplate restTemplate;
    private String url = "http://localhost:8080";

    @Override
    public Employee addEmployee(Employee employee) {
        return restTemplate.postForObject(
                url + "/employee",
                employee,
                Employee.class
        );
    }
}
