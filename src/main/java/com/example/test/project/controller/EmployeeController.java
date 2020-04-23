package com.example.test.project.controller;

import com.example.test.project.entity.Employee;
import com.example.test.project.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class EmployeeController {
    @Autowired
    EmployeeService service;

    @GetMapping("/showEmployees")
    public String findEmployees(Model model) {
        List<Employee> employees = service.findAll();
        model.addAttribute("employees", employees);

        return "showEmployees";
    }
}
