package com.hamid.horecapilot.staff.repository;

import com.hamid.horecapilot.staff.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
