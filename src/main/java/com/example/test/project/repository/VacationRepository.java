package com.example.test.project.repository;

import com.example.test.project.entity.Vacation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;

public interface VacationRepository extends PagingAndSortingRepository<Vacation, Long>, JpaSpecificationExecutor<Vacation> {

    List<Vacation> findAllByEmployee_EmployeeId(Long employeeId);

    List<Vacation> findAllByVacationStartDateAfter(PageRequest pageRequest, LocalDate vacationStartDate);
    List<Vacation> findAllByVacationEndDateBefore(PageRequest pageRequest, LocalDate vacationStartDate);
    List<Vacation> findAllByVacationStartDateAfterAndVacationEndDateBefore(PageRequest pageRequest, LocalDate vacationStartDate, LocalDate vacationEndDate);
}
