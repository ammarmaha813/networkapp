package net.thevpc.samples.springnuts.moduleai.dal.repository;

import net.thevpc.samples.springnuts.moduleai.dal.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
}
