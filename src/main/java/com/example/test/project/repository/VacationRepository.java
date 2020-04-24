package com.example.test.project.repository;

import com.example.test.project.entity.Vacation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface VacationRepository extends PagingAndSortingRepository<Vacation, Long>, JpaSpecificationExecutor<Vacation> {

    List<Vacation> findAllByEmployee_EmployeeId(Long employeeId);
}
