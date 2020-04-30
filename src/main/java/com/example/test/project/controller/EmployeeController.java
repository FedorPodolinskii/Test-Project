package com.example.test.project.controller;

import com.example.test.project.entity.Employee;
import com.example.test.project.exception.BadResourceException;
import com.example.test.project.exception.ResourceNotFoundException;
import com.example.test.project.model.EmployeeListPage;
import com.example.test.project.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EmployeeController {
    final EmployeeService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @RequestMapping(value = "/employeeList", produces = {
            MediaType.TEXT_HTML_VALUE},
            method = RequestMethod.GET)
    public String viewEmployees() {
        return "employee-list";
    }

    @ResponseBody
    @GetMapping(value = "/employees")
    public ResponseEntity<EmployeeListPage> getEmployees(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
                                                         @RequestParam(value = "rowsPerPage", defaultValue = "10") int rowsPerPage,
                                                         @RequestParam(value = "ascending", defaultValue = "true") boolean ascending,
                                                         @RequestParam(value = "sortByColumn", defaultValue = "employeeId") String sortByColumn) {
        List<Employee> employees = service.findAll(pageNumber, rowsPerPage, ascending, sortByColumn);

        long count = service.count();
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = (pageNumber * rowsPerPage) < count;
        EmployeeListPage employeeListPage = new EmployeeListPage(hasPrev, hasNext, employees, pageNumber, rowsPerPage, ascending, sortByColumn);
        return new ResponseEntity<>(employeeListPage, HttpStatus.OK);
    }

    @GetMapping(value = "/employees/{employeeId}")
    public String getEmployeeById(Model model, @PathVariable long employeeId) {
        Employee employee = null;
        try {
            employee = service.findById(employeeId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Сотрудник не найден");
        }
        model.addAttribute("employee", employee);

        return "employee";
    }

    @GetMapping(value = {"/employees/add"})
    public String showAddEmployee(Model model) {
        Employee employee = new Employee();
        model.addAttribute("add", true);
        model.addAttribute("employee", employee);

        return "employee-edit";
    }

    @PostMapping(value = "/employees/add")
    public String addEmployee(Model model,
                              @ModelAttribute("employee") Employee employee) {

        try {
            Employee newEmployee = service.save(employee);
            return "redirect:/employees/" + String.valueOf(newEmployee.getEmployeeId());
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("add", true);

            return "employee-edit";
        }
    }

    @GetMapping(value = {"/employees/{employeeId}/edit"})
    public String showEditEmployee(Model model, @PathVariable long employeeId) {
        Employee employee = null;
        try {
            employee = service.findById(employeeId);
            employee.setPassword("");
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Employee not found");
        }
        model.addAttribute("add", false);
        model.addAttribute("employee", employee);

        return "employee-edit";
    }

    @PostMapping(value = {"/employees/{employeeId}/edit"})
    public String updateEmployee(Model model,
                                 @PathVariable Long employeeId,
                                 @ModelAttribute Employee employee) {
        try {
            employee.setEmployeeId(employeeId);
            service.update(employee);
            return "redirect:/employees/" + String.valueOf(employee.getEmployeeId());

        } catch (BadResourceException | ResourceNotFoundException ex) {
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);

            model.addAttribute("add", false);
            return "employee-edit";
        }
    }

    @GetMapping(value = {"/employees/{employeeId}/delete"})
    public String showDeleteEmployeeById(
            Model model, @PathVariable long employeeId) {
        Employee employee = null;
        try {
            employee = service.findById(employeeId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Сотрудник не найден");
        }
        model.addAttribute("allowDelete", true);
        model.addAttribute("employee", employee);

        return "employee";
    }

    @PostMapping(value = {"/employees/{employeeId}/delete"})
    public String deleteEmployeeById(
            Model model, @PathVariable long employeeId) {
        try {
            service.deleteById(employeeId);
            return "redirect:/employeeList";
        } catch (ResourceNotFoundException ex) {
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);

            return "employee";
        }
    }
}
