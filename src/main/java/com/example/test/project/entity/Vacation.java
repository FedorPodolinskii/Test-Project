package com.example.test.project.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "VACATION", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ID")})
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long vacationId;
    @Column(name = "START_DATE", unique = false, nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate vacationStartDate;
    @Column(name = "END_DATE", unique = false, nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate vacationEndDate;
    @ManyToOne
    private Employee employee;

    public Vacation() {
    }

    public Vacation(LocalDate vacationStartDate, LocalDate vacationEndDate, Employee employee) {
        this.vacationStartDate = vacationStartDate;
        this.vacationEndDate = vacationEndDate;
        this.employee = employee;
    }

    public void setVacationId(Long vacationId) {
        this.vacationId = vacationId;
    }

    public Long getVacationId() {
        return vacationId;
    }

    public LocalDate getVacationStartDate() {
        return vacationStartDate;
    }

    public void setVacationStartDate(LocalDate vacationStartDate) {
        this.vacationStartDate = vacationStartDate;
    }

    public LocalDate getVacationEndDate() {
        return vacationEndDate;
    }

    public void setVacationEndDate(LocalDate vacationEndDate) {
        this.vacationEndDate = vacationEndDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
