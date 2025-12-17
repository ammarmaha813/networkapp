package net.thevpc.samples.springnuts.moduleai.service.impl.entity;

import net.thevpc.nuts.util.NAssert;
import net.thevpc.samples.springnuts.moduleai.dal.entity.EmployeeEntity;
import net.thevpc.samples.springnuts.moduleai.dal.repository.EmployeeRepository;
import net.thevpc.samples.springnuts.moduleai.model.Employee;
import net.thevpc.samples.springnuts.moduleai.service.impl.converter.EmployeeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    public Employee addEmployee(Employee employee) {
        NAssert.requireTrue(employee != null, "employee must not be null");
        EmployeeEntity employeeEntity = EmployeeConverter.INSTANCE.toEntity(employee);
        employeeRepository.save(employeeEntity) ;
        employee.setId(employeeEntity.getId());
        return employee;
    }

}
