package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.entity.Employee;
import com.education.mypaymentservice.model.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByEmail(String email);

    int countByRole(Roles role);
}

