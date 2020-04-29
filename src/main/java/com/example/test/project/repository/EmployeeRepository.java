package com.example.test.project.repository;

import com.example.test.project.entity.Employee;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Employee findByLogin(String login);
}
