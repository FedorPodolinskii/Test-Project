package com.example.test.project.controller;

import com.example.test.project.entity.Employee;
import com.example.test.project.entity.Vacation;
import com.example.test.project.exception.BadResourceException;
import com.example.test.project.exception.ResourceNotFoundException;
import com.example.test.project.model.VacationListPage;
import com.example.test.project.service.EmployeeService;
import com.example.test.project.service.VacationService;
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
public class VacationController {
    final EmployeeService employeeService;
    final VacationService vacationService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public VacationController(EmployeeService employeeService, VacationService vacationService) {
        this.employeeService = employeeService;
        this.vacationService = vacationService;
    }

    @ResponseBody
    @GetMapping(value = "/vacationsData")
    public ResponseEntity<VacationListPage> getVacations(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
                                                         @RequestParam(value = "rowsPerPage", defaultValue = "10") int rowsPerPage,
                                                         @RequestParam(value = "ascending", defaultValue = "true") boolean ascending,
                                                         @RequestParam(value = "sortByColumn", defaultValue = "vacationStartDate") String sortByColumn,
                                                         @RequestParam(value = "filterWord", defaultValue = "") String filterWord,
                                                         @RequestParam(value = "startFilterDate", defaultValue = "") String startFilterDate,
                                                         @RequestParam(value = "endFilterDate", defaultValue = "") String endFilterDate
    ) {

        List<Vacation> vacations = vacationService.findAll(pageNumber, rowsPerPage, ascending, sortByColumn, filterWord,startFilterDate,endFilterDate);

        long count = vacationService.count();
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = (pageNumber * rowsPerPage) < count;
        VacationListPage vacationListPage = new VacationListPage(hasPrev, hasNext, vacations, pageNumber, rowsPerPage, ascending, sortByColumn);
        return new ResponseEntity<>(vacationListPage, HttpStatus.OK);
    }

    @RequestMapping(value = "/vacations", produces = {
            MediaType.TEXT_HTML_VALUE},
            method = RequestMethod.GET)
    public String viewVacations() {
        return "vacation-list";
    }

    @GetMapping(value = "/employees/{employeeId}/vacations")
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

        return "vacations-of-employee";
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
            vacation.setEmployee(employee);
            vacationService.save(vacation);
            return "redirect:/employees/" + String.valueOf(employee.getEmployeeId()) + "/vacations";
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
    public String updateVacation(Model model,
                                 @PathVariable Long employeeId, @PathVariable Long vacationId,
                                 @ModelAttribute Employee employee, @ModelAttribute Vacation vacation) {
        try {
            vacation.setVacationId(vacationId);
            vacation.setEmployee(employee);
            vacationService.update(vacation);
            return "redirect:/employees/" + String.valueOf(employeeId) + "/vacations";

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
            return "redirect:/employees/" + String.valueOf(employeeId) + "/vacations";
        } catch (ResourceNotFoundException ex) {
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);

            return "vacation";
        }
    }
}