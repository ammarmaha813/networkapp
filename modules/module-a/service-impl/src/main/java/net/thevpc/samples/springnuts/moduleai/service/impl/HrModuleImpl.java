package net.thevpc.samples.springnuts.moduleai.service.impl;

import lombok.experimental.Delegate;
import net.thevpc.samples.springnuts.moduleai.service.impl.entity.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HrModuleImpl implements HrModule {
    @Autowired
    @Delegate
    private EmployeeService employeeService;
}
