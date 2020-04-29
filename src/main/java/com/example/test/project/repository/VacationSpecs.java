package com.example.test.project.repository;

import com.example.test.project.entity.Vacation;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class VacationSpecs {
    public static Specification<Vacation> getVacationsFilteredSpec(String filterWord, String startFilterDate, String endFilterDate){
        return new Specification<Vacation>() {
            @Override
            public Predicate toPredicate(Root<Vacation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                Predicate predicateEmployeeName = criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("EMPLOYEE").get("FULL_NAME")),criteriaBuilder.parameter(String.class,filterWord));
                Predicate before = criteriaBuilder.lessThan(root.get("START_DATE"),startFilterDate);
                Predicate after = criteriaBuilder.greaterThan(root.get("END_DATE"),endFilterDate);

                return criteriaBuilder.and(predicateEmployeeName,before,after);
            }
        };
    }
}
