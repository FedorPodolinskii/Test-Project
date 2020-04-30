package com.example.test.project.service;

import com.example.test.project.entity.Employee;
import com.example.test.project.exception.BadResourceException;
import com.example.test.project.exception.ResourceAlreadyExistsException;
import com.example.test.project.exception.ResourceNotFoundException;
import com.example.test.project.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repository;
    private BCryptPasswordEncoder encoder;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setEncoder(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public List<Employee> findAll(int pageNumber, int rowsPerPage, boolean ascending, String sortByColumn) {
        List<Employee> employees = new ArrayList<>();
        Pageable pageRequest;
        if (ascending) {
            pageRequest = PageRequest.of(pageNumber - 1, rowsPerPage,
                    Sort.by(sortByColumn).ascending());
        } else {
            pageRequest = PageRequest.of(pageNumber - 1, rowsPerPage,
                    Sort.by(sortByColumn).descending());
        }
        repository.findAll(pageRequest).forEach(employees::add);
        return employees;
    }

    public TreeMap<String, Long> calculateFreeDays() {
        TreeMap<String, Long> freeDays = new TreeMap<>();
        List<Employee> employees = (List<Employee>) repository.findAll(Sort.by("fullName"));
        for (Employee employee : employees) {
            LocalDate startDate = employee.getStartDate();
            LocalDate today = LocalDate.now();
            long numberOfFreeDays = ChronoUnit.YEARS.between(startDate, today) * 28;
            String fullName = employee.getFullName();
            freeDays.merge(fullName, numberOfFreeDays, Long::sum);
        }
        return freeDays;
    }

    @Override
    public void update(Employee employee) throws BadResourceException, ResourceNotFoundException {
        if (!StringUtils.isEmpty(employee.getFullName())) {
            if (!existsById(employee.getEmployeeId())) {
                throw new ResourceNotFoundException("Не удалось найти сотрудника с ID: " + employee.getEmployeeId());
            }
            if (!employee.getPassword().isEmpty()) {
                String encPass = encoder.encode(employee.getPassword());
                employee.setPassword(encPass);
            } else {
                repository.findById(employee.getEmployeeId()).ifPresent(value -> employee.setPassword(value.getPassword()));
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
            if (ChronoUnit.YEARS.between(employee.getDateOfBirth(), employee.getStartDate()) < 18) {
                BadResourceException exc = new BadResourceException("Сотруднику меньше 18 лет в момент приёма на работу");
                exc.addErrorMessage("Информация заполнена неверно");
                throw exc;
            }
            if (ChronoUnit.YEARS.between(employee.getDateOfBirth(), LocalDate.now()) < 18) {
                BadResourceException exc = new BadResourceException("Сотруднику меньше 18 лет на сегодняшний день - " + LocalDate.now().toString());
                exc.addErrorMessage("Информация заполнена неверно");
                throw exc;
            }
            if (employee.getDateOfBirth().isAfter(employee.getStartDate()) && employee.getDateOfBirth().isEqual(employee.getStartDate())) {
                BadResourceException exc = new BadResourceException("Дата рождения меньше даты приёма на работу");
                exc.addErrorMessage("Информация заполнена неверно");
                throw exc;
            }
            if (StringUtils.isEmpty(employee.getPassword())) {
                BadResourceException exc = new BadResourceException("Пароль не может быть пустым");
                exc.addErrorMessage("Информация заполнена неверно");
                throw exc;
            }

            String encPass = encoder.encode(employee.getPassword());
            employee.setPassword(encPass);
            return repository.save(employee);
        } else {
            BadResourceException exc = new BadResourceException("Не удалось создать сотрудника");
            exc.addErrorMessage("Информация заполнена неверно");
            throw exc;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Employee employee = repository.findByLogin(login);
        if (employee == null) {
            throw new UsernameNotFoundException("Невероное имя пользователя или пароль");
        }
        return new User(employee.getLogin(), employee.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("Default")));
    }
}
