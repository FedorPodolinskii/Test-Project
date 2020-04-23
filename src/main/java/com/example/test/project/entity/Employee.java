package com.example.test.project.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "EMPLOYEE", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ID"),
        @UniqueConstraint(columnNames = "LOGIN")})
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long employeeId;
    @Column(name = "FULL_NAME", unique = false, nullable = false)
    private String fullName;
    @Column(name = "BIRTH_DATE", unique = false, nullable = false)
    private LocalDate dateOfBirth;
    @Column(name = "POSITION", unique = false, nullable = false)
    private String position;
    @Column(name = "START_DATE", unique = false, nullable = false)
    private LocalDate startDate;
    @Column(name = "LOGIN", unique = true, nullable = false)
    private String login;
    @Column(name = "PASSWORD", unique = false, nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "EMPLOYEE_ID")
    private Set<Vacation> vacations;

    public Employee() {
    }

    public Employee(String fullName, LocalDate dateOfBirth, String position, LocalDate startDate, String login, String password) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.position = position;
        this.startDate = startDate;
        this.login = login;
        this.password = password;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setVacations(Set<Vacation> vacations) {
        this.vacations = vacations;
    }

    public Set<Vacation> getVacations() {
        return vacations;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
