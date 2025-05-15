package com.damian3111.recruitment_manager_api.persistence.specification;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import jakarta.persistence.criteria.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Iterator;

@Component
public class CandidatesSpecification implements BaseSpecification<CandidateEntity, Long>{

//    public Specification<CandidateEntity> beforeAppliedDate(String path, String search) {
//        return (root, cq, cb) -> {
//            Path<?> pathToObject = this.resolvePathToObject(root, path);
//            return this.getPredicateBeforeAppliedDate(cb, pathToObject, search);
//        };
//    }
//    private Predicate getPredicateBeforeAppliedDate(CriteriaBuilder cb, Path<?> path, String search) {
//            Expression<String> dateStringExpr = cb.function("TO_CHAR", String.class, path, cb.literal("'YYYY-MM-DD'"));
//            return cb.like(cb.lower(dateStringExpr), this.getLowerCaseSearchTerm(search));
//    }


    public Specification<CandidateEntity> beforeAppliedDate(String path, LocalDate search) {
        return (root, cq, cb) -> {
            Path<?> pathToDate = this.resolvePathToObject(root, path);
            return this.getPredicateBeforeAppliedDate(cb, (Path<LocalDate>)pathToDate, search);
        };
    }

    public Specification<CandidateEntity> afterAppliedDate(String path, LocalDate search) {
        return (root, cq, cb) -> {
            Path<?> pathToDate = this.resolvePathToObject(root, path);
            return this.getPredicateAfterAppliedDate(cb, (Path<LocalDate>)pathToDate, search);
        };
    }

    private Predicate getPredicateBeforeAppliedDate(CriteriaBuilder cb, Path<LocalDate> path, LocalDate search) {
        return cb.lessThanOrEqualTo(path, search);
    }

    private Predicate getPredicateAfterAppliedDate(CriteriaBuilder cb, Path<LocalDate> path, LocalDate search) {
        return cb.greaterThanOrEqualTo(path, search);
    }
}
