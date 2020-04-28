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

    @Override
    public List<Employee> findAll(int pageNumber, int rowsPerPage, boolean ascending, String sortByColumn) {
        List<Employee> employees = new ArrayList<>();
        Pageable pageRequest;
        if(ascending){
            pageRequest = PageRequest.of(pageNumber - 1, rowsPerPage,
                    Sort.by(sortByColumn).ascending());
        } else {
            pageRequest = PageRequest.of(pageNumber - 1, rowsPerPage,
                    Sort.by(sortByColumn).descending());
        }
        repository.findAll(pageRequest).forEach(employees::add);
        return employees;
    }

    @Override
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

    @Override
    public boolean existsById(Long aLong) {
        return repository.existsById(aLong);
    }

    @Override
    public Employee findById(Long employeeId) throws ResourceNotFoundException {
        Employee employee = repository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new ResourceNotFoundException("Не удалось найти сотрудника с ID: " + employeeId);
        } else return employee;
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(Long employeeId) throws ResourceNotFoundException {
        if (!existsById(employeeId)) {
            throw new ResourceNotFoundException("Не удалось найти сотрудника с ID: " + employeeId);
        } else {
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
        } else {
            BadResourceException exc = new BadResourceException("Не удалось создать сотрудника");
            exc.addErrorMessage("Информация заполнена неверно");
            throw exc;
        }
    }
}
