package com.example.test.project.service;

import com.example.test.project.entity.Employee;
import com.example.test.project.exception.BadResourceException;
import com.example.test.project.exception.ResourceAlreadyExistsException;
import com.example.test.project.exception.ResourceNotFoundException;
import com.example.test.project.repository.EmployeeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> findAll(int pageNumber, int rowPerPage) {
        List<Employee> employees = new ArrayList<>();
        Pageable sortedByIdAsc = PageRequest.of(pageNumber - 1, rowPerPage,
                Sort.by("employeeId").ascending());
        repository.findAll(sortedByIdAsc).forEach(employees::add);
        return employees;
    }


    public void update(Employee employee) throws BadResourceException, ResourceNotFoundException {
        if (!StringUtils.isEmpty(employee.getFullName())) {
            if (!existsById(employee.getEmployeeId())) {
                throw new ResourceNotFoundException("Не удалось найти сотрудника с ID: " + employee.getEmployeeId());
            }
            repository.save(employee);
        } else {
            BadResourceException exc = new BadResourceException("Не удалось сохранить сотрудника");
            exc.addErrorMessage("Информация заполнена неверно");
            throw exc;
        }
    }

    public boolean existsById(Long aLong) {
        return repository.existsById(aLong);
    }

    public Employee findById(Long employeeId) throws ResourceNotFoundException {
        Employee employee = repository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new ResourceNotFoundException("Не удалось найти сотрудника с ID: " + employeeId);
        } else return employee;
    }

    public long count() {
        return repository.count();
    }

    public void deleteById(Long employeeId) throws ResourceNotFoundException{
        if (!existsById(employeeId)) {
            throw new ResourceNotFoundException("Не удалось найти сотрудника с ID: " + employeeId);
        }
        else {
            repository.deleteById(employeeId);
        }
    }

    @Override
    public Employee save(Employee employee) throws BadResourceException, ResourceAlreadyExistsException {
        if (!StringUtils.isEmpty(employee.getFullName())) {
            if (employee.getEmployeeId() != null && existsById(employee.getEmployeeId())) {
                throw new ResourceAlreadyExistsException("Сотрудник с ID: " + employee.getEmployeeId() +
                        " уже существует");
            }
            return repository.save(employee);
        }
        else {
            BadResourceException exc = new BadResourceException("Не удалось создать сотрудника");
            exc.addErrorMessage("Информация заполнена неверно");
            throw exc;
        }
    }
}
