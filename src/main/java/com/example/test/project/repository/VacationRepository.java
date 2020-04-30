package com.example.test.project.repository;

import com.example.test.project.entity.Vacation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;

public interface VacationRepository extends PagingAndSortingRepository<Vacation, Long>, JpaSpecificationExecutor<Vacation> {

    List<Vacation> findAll(Sort sort);

    List<Vacation> findAllByEmployee_EmployeeId(Long employeeId);

    Page<Vacation> findAllByEmployee_FullNameContainsIgnoreCaseAndVacationStartDateAfterAndVacationEndDateBefore(String employee_fullName, LocalDate vacationStartDate, LocalDate vacationEndDate, Pageable pageable);

    Page<Vacation> findAllByVacationStartDateAfterAndVacationEndDateBefore(LocalDate vacationStartDate, LocalDate vacationEndDate, Pageable pageable);

    Page<Vacation> findAllByVacationStartDateAfter(LocalDate vacationStartDate, Pageable pageable);

    Page<Vacation> findAllByVacationEndDateBefore(LocalDate vacationEndDate, Pageable pageable);

    Page<Vacation> findAllByEmployee_FullNameContainsIgnoreCaseAndVacationStartDateAfter(String employee_fullName, LocalDate vacationStartDate, Pageable pageable);

    Page<Vacation> findAllByEmployee_FullNameContainsIgnoreCaseAndVacationEndDateBefore(String employee_fullName, LocalDate vacationEndDate, Pageable pageable);

    Page<Vacation> findAllByEmployee_FullNameContainsIgnoreCase(String employee_fullName, Pageable pageable);
}
