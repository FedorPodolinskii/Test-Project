package com.example.test.project.service;

import com.example.test.project.entity.Employee;
import com.example.test.project.exception.BadResourceException;
import com.example.test.project.exception.ResourceAlreadyExistsException;
import com.example.test.project.exception.ResourceNotFoundException;

import java.util.List;

public interface EmployeeService {

    public boolean existsById(Long aLong);

    List<Employee> findAll(int pageNumber, int rowPerPage);

    Employee findById(Long employeeId) throws ResourceNotFoundException;

    void update(Employee employee) throws BadResourceException, ResourceNotFoundException;

    long count();

    void deleteById(Long aLong) throws ResourceNotFoundException;

    Employee save(Employee employee) throws BadResourceException, ResourceAlreadyExistsException;
}
