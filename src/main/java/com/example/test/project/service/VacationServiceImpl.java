package com.example.test.project.service;

import com.example.test.project.entity.Vacation;
import com.example.test.project.exception.BadResourceException;
import com.example.test.project.exception.ResourceAlreadyExistsException;
import com.example.test.project.exception.ResourceNotFoundException;
import com.example.test.project.repository.VacationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VacationServiceImpl implements VacationService {
    private final VacationRepository repository;

    public VacationServiceImpl(VacationRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(Long aLong) {
        return repository.existsById(aLong);
    }

    @Override
    public List<Vacation> findAll(int pageNumber, int rowsPerPage, boolean ascending, String sortByColumn, String filterWord, String startFilterDate, String endFilterDate) {
        Sort sort;
        if (ascending) {
            sort = Sort.by(sortByColumn).ascending();
        } else {
            sort = Sort.by(sortByColumn).descending();
        }
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, rowsPerPage, sort);
        List<Vacation> vacations = new ArrayList<>();
        if (!filterWord.isEmpty()) {
            if (!startFilterDate.isEmpty()) {
                if (!endFilterDate.isEmpty()) {
                    repository.findAllByEmployee_FullNameContainsIgnoreCaseAndVacationStartDateAfterAndVacationEndDateBefore(filterWord, LocalDate.parse(startFilterDate), LocalDate.parse(endFilterDate), pageRequest).forEach(vacations::add);
                } else {
                    repository.findAllByEmployee_FullNameContainsIgnoreCaseAndVacationStartDateAfter(filterWord, LocalDate.parse(startFilterDate), pageRequest).forEach(vacations::add);
                }
            } else {
                if (!endFilterDate.isEmpty()) {
                    repository.findAllByEmployee_FullNameContainsIgnoreCaseAndVacationEndDateBefore(filterWord, LocalDate.parse(endFilterDate), pageRequest).forEach(vacations::add);
                } else {
                    repository.findAllByEmployee_FullNameContainsIgnoreCase(filterWord, pageRequest).forEach(vacations::add);
                }
            }
        } else {
            if (!startFilterDate.isEmpty()) {
                if (!endFilterDate.isEmpty()) {
                    repository.findAllByVacationStartDateAfterAndVacationEndDateBefore(LocalDate.parse(startFilterDate), LocalDate.parse(endFilterDate), pageRequest).forEach(vacations::add);
                } else {
                    repository.findAllByVacationStartDateAfter(LocalDate.parse(startFilterDate), pageRequest).forEach(vacations::add);
                }
            } else {
                if (!endFilterDate.isEmpty()) {
                    repository.findAllByVacationEndDateBefore(LocalDate.parse(endFilterDate), pageRequest).forEach(vacations::add);
                } else {
                    repository.findAll(pageRequest).forEach(vacations::add);
                }
            }
        }
        return vacations;
    }

    @Override
    public List<Vacation> findAll() {
        return repository.findAll(Sort.by("vacationStartDate").ascending());
    }

    @Override
    public List<Vacation> findAllByEmployeeId(Long employeeId) throws ResourceNotFoundException {
        List<Vacation> vacations;
        vacations = repository.findAllByEmployee_EmployeeId(employeeId);
        if (vacations == null) {
            throw new ResourceNotFoundException("Не удалось найти записи об отпуске сотрудника с ID: " + employeeId);
        } else return vacations;
    }

    @Override
    public Vacation findById(Long vacationId) throws ResourceNotFoundException {
        Vacation vacation = repository.findById(vacationId).orElse(null);
        if (vacation == null) {
            throw new ResourceNotFoundException("Не удалось найти запись об отпуске с ID: " + vacationId);
        } else return vacation;
    }

    @Override
    public void update(Vacation vacation) throws BadResourceException, ResourceNotFoundException {
        if (vacation.getEmployee() != null) {
            if (!existsById(vacation.getVacationId())) {
                throw new ResourceNotFoundException("Не удалось найти запись об отпуске с ID: " + vacation.getVacationId());
            }
            repository.save(vacation);
        } else {
            BadResourceException exc = new BadResourceException("Не удалось сохранить отпуск");
            exc.addErrorMessage("Информация заполнена неверно");
            throw exc;
        }
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(Long vacationId) throws ResourceNotFoundException {
        if (!existsById(vacationId)) {
            throw new ResourceNotFoundException("Не удалось найти запись об отпуске с ID: " + vacationId);
        } else {
            repository.deleteById(vacationId);
        }
    }

    @Override
    public void save(Vacation vacation) throws BadResourceException, ResourceAlreadyExistsException {
        if (vacation.getEmployee() != null) {
            if (vacation.getVacationId() != null && existsById(vacation.getVacationId())) {
                throw new ResourceAlreadyExistsException("Отпуск с ID: " + vacation.getVacationId() +
                        " уже существует");
            }
            repository.save(vacation);
        } else {
            BadResourceException exc = new BadResourceException("Не удалось создать отпуск");
            exc.addErrorMessage("Информация заполнена неверно");
            throw exc;
        }
    }
}
