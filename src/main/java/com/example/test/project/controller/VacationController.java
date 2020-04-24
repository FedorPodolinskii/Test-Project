package com.example.test.project.controller;

import com.example.test.project.entity.Employee;
import com.example.test.project.entity.Vacation;
import com.example.test.project.exception.BadResourceException;
import com.example.test.project.exception.ResourceNotFoundException;
import com.example.test.project.service.EmployeeService;
import com.example.test.project.service.VacationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class VacationController {
    final EmployeeService employeeService;

    final VacationService vacationService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int ROW_PER_PAGE = 10;

    public VacationController(EmployeeService employeeService, VacationService vacationService) {
        this.employeeService = employeeService;
        this.vacationService = vacationService;
    }

    @GetMapping(value = "/vacations")
    public String getEmployees(Model model,
                               @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
        List<Vacation> vacations = vacationService.findAll(pageNumber, ROW_PER_PAGE);

        long count = vacationService.count();
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = (pageNumber * ROW_PER_PAGE) < count;
        model.addAttribute("vacations", vacations);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("prev", pageNumber - 1);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("next", pageNumber + 1);

        return "vacation-list";
    }

/*    @GetMapping(value = "/vacations/{vacationId}")
    public String getEmployeeById(Model model, @PathVariable long vacationId) {
        Vacation vacation = null;
        try {
            vacation = vacationService.findById(vacationId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Сотрудник не найден");
        }
        model.addAttribute("vacation", vacation);

        return "employee";
    }*/

    @GetMapping(value = "/employees/{employeeId}/vacationList")
    public String getEmployeeVacationList(Model model, @PathVariable long employeeId) {
        List<Vacation> vacations = null;
        Employee employee = null;
        try {
            vacations = vacationService.findAllByEmployeeId(employeeId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Нет записей об отпусках");
        }
        try {
            employee = employeeService.findById(employeeId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Сотрудник не найден");
        }
        model.addAttribute("vacations", vacations);
        model.addAttribute("employee", employee);

        return "vacation-list-of-employee";
    }

    @GetMapping(value = {"/employees/{employeeId}/addVacation"})
    public String showAddVacation(Model model, @PathVariable long employeeId) {
        Vacation vacation = new Vacation();
        Employee employee = null;
        try {
            employee = employeeService.findById(employeeId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Сотрудник не найден");
        }
        model.addAttribute("add", true);
        model.addAttribute("vacation", vacation);
        model.addAttribute("employee", employee);

        return "vacation-edit";
    }

    @PostMapping(value = "/employees/{employeeId}/addVacation")
    public String addVacation(Model model,
                              @ModelAttribute("vacation") Vacation vacation, @ModelAttribute("employee") Employee employee) {

        try {
            Vacation newVacation = vacationService.save(vacation);
            return "redirect:/employees/" + String.valueOf(employee.getEmployeeId()) + "/vacationList";
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("add", true);

            return "vacation-edit";
        }
    }

    @GetMapping(value = {"/employees/{employeeId}/editVacation/{vacationId}"})
    public String showEditEmployee(Model model, @PathVariable long employeeId, @PathVariable long vacationId) {
        Employee employee = null;
        try {
            employee = employeeService.findById(employeeId);
        } catch (ResourceNotFoundException e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
        }
        Vacation vacation = null;
        try {
            vacation = vacationService.findById(vacationId);
        } catch (ResourceNotFoundException e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
        }

        model.addAttribute("add", false);
        model.addAttribute("employee", employee);
        model.addAttribute("vacation", vacation);

        return "vacation-edit";
    }

    @PostMapping(value = {"/employees/{employeeId}/editVacation/{vacationId}"})
    public String updateEmployee(Model model,
                                 @PathVariable Long employeeId, @PathVariable Long vacationId,
                                 @ModelAttribute Employee employee, @ModelAttribute Vacation vacation) {
        try {
            vacation.setVacationId(vacationId);
            vacationService.update(vacation);
            return "redirect:/employees/" + String.valueOf(employeeId) + "/vacationList";

        } catch (BadResourceException | ResourceNotFoundException ex) {
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("add", false);

            return "vacation-edit";
        }
    }

    @GetMapping(value = {"/employees/{employeeId}/deleteVacation/{vacationId}"})
    public String showDeleteVacationById(
            Model model, @PathVariable long employeeId, @PathVariable long vacationId) {
        Employee employee = null;
        try {
            employee = employeeService.findById(employeeId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Сотрудник не найден");
        }
        Vacation vacation = null;
        try {
            vacation = vacationService.findById(vacationId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Отпуск не найден");
        }
        model.addAttribute("allowDelete", true);
        model.addAttribute("employee", employee);
        model.addAttribute("vacation", vacation);

        return "vacation";
    }

    @PostMapping(value = {"/employees/{employeeId}/deleteVacation/{vacationId}"})
    public String deleteVacationById(
            Model model, @PathVariable long employeeId, @PathVariable long vacationId) {
        try {
            vacationService.deleteById(vacationId);
            return "redirect:/employees/" + String.valueOf(employeeId) + "/vacationList";
        } catch (ResourceNotFoundException ex) {
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);

            return "vacation";
        }
    }
}
