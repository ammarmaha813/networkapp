package net.thevpc.samples.springnuts.moduleai.service.impl.converter;

import net.thevpc.samples.springnuts.moduleai.dal.entity.EmployeeEntity;
import net.thevpc.samples.springnuts.moduleai.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeConverter {
    public EmployeeConverter INSTANCE = Mappers.getMapper(EmployeeConverter.class);
    public EmployeeEntity toEntity(Employee e);
    public Employee fromEntity(EmployeeEntity e);
}
