package com.example.test.project.service;

import com.example.test.project.entity.Vacation;
import com.example.test.project.exception.BadResourceException;
import com.example.test.project.exception.ResourceAlreadyExistsException;
import com.example.test.project.exception.ResourceNotFoundException;

import java.util.List;

public interface VacationService {

    public boolean existsById(Long aLong);

    List<Vacation> findAllByEmployeeId(Long employeeId) throws ResourceNotFoundException;

    Vacation findById(Long vacationId) throws ResourceNotFoundException;

    void update(Vacation vacation) throws BadResourceException, ResourceNotFoundException;

    long count();

    void deleteById(Long vacationId) throws ResourceNotFoundException;

    Vacation save(Vacation vacation) throws BadResourceException, ResourceAlreadyExistsException;

    List<Vacation> findAll(int pageNumber, int rowsPerPage, boolean ascending, String sortByColumn, String filterWord, String startFilterDate, String endFilterDate);
}
