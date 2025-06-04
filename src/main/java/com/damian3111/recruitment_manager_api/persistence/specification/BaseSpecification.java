package com.damian3111.recruitment_manager_api.persistence.specification;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkill;
import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
import jakarta.persistence.criteria.*;
import org.openapitools.model.JobDtoSkillsInner;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public interface BaseSpecification <T, K> {

    Specification<?> EMPTY = Specification.where(null);

    default String getLowerCaseSearchTerm(String search) {
        return "%" + Optional.ofNullable(search).orElse("").toLowerCase().trim() + "%";
    }

    default Specification<T> empty() {
        return (Specification<T>) EMPTY;
    }

    default Specification<T> idEquals(K id) {
        return (root, cq, cb) -> cb.equal(root.get("id"), id);
    }

    default Specification<T> idNotEquals(K id) {
        return (root, cq, cb) -> cb.notEqual(root.get("id"), id);
    }

    default Specification<T> idIn(List<K> ids) {
        return (root, cq, cb) -> root.get("id").in(ids);
    }

    default Specification<T> idLikeIgnoreCase(String search) {
        return (root, cq, cb) -> cb.like(cb.lower(root.get("id").as(String.class)), this.getLowerCaseSearchTerm(search));
    }


    default Specification<T> propertyLikeIgnoreCase(String path, String search) {
        return (root, cq, cb) -> {
            Path<?> pathToObject = this.resolvePathToObject(root, path);
            return this.getPredicateLikeIgnoreCase(cb, pathToObject, search);
        };
    }

    default Specification<T> propertyGreaterOrEqual(String path, Integer search) {
        return (root, cq, cb) -> {
            Path<?> pathToObject = this.resolvePathToObject(root, path);
            return this.getPredicateGreaterOrEqual(cb, pathToObject, search);
        };
    }

    default Specification<T> propertyLessOrEqual(String path, Integer search) {
        return (root, cq, cb) -> {
            Path<?> pathToObject = this.resolvePathToObject(root, path);
            return this.getPredicateLessOrEqual(cb, pathToObject, search);
        };
    }

    default Predicate getPredicateLikeIgnoreCase(CriteriaBuilder cb, Path<?> path, String search) {
        if (path.getJavaType().equals(LocalDate.class)) {
            Expression<String> dateStringExpr = cb.function("TO_CHAR", String.class, path, cb.literal("'YYYY-MM-DD HH24:MI'"));
            return cb.like(cb.lower(dateStringExpr), this.getLowerCaseSearchTerm(search));
        } else {
            return cb.like(cb.lower(path.as(String.class)), this.getLowerCaseSearchTerm(search));
        }
    }

    default Specification<T> propertyInSkills(List<JobDtoSkillsInner> skills) {
        return (root, cq, cb) -> {
            if (skills == null || skills.isEmpty()) {
                return cb.conjunction();
            }

            Join<T, CandidateSkill> candidateSkillJoin = root.join("skills");
            Join<CandidateSkill, SkillEntity> skillJoin = candidateSkillJoin.join("skill");

            List<Predicate> predicates = skills.stream()
                    .filter(skill -> skill.getName() != null) // Skip null names
                    .map(skill -> {
                        Predicate namePredicate = cb.equal(
                                cb.lower(skillJoin.get("name")),
                                skill.getName().toLowerCase()
                        );

                        if (skill.getProficiencyLevel() != null) {
                            return cb.and(
                                    namePredicate,
                                    cb.equal(
                                            candidateSkillJoin.get("proficiencyLevel"),
                                            skill.getProficiencyLevel().toString() // Convert enum to String
                                    )
                            );
                        }
                        return namePredicate;
                    })
                    .collect(Collectors.toList());

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
    default Predicate getPredicateGreaterOrEqual(CriteriaBuilder cb, Path<?> path, Integer search) {
            return cb.greaterThanOrEqualTo(path.as(Integer.class), search);
    }

    default Predicate getPredicateLessOrEqual(CriteriaBuilder cb, Path<?> path, Integer search) {
            return cb.lessThanOrEqualTo(path.as(Integer.class), search);
    }


    default Path<?> resolvePathToObject(Root<T> root, String path) {
        Iterator<String> iterator = Arrays.asList(path.split("\\.")).iterator();
        Path<?> objectPath = root.get(iterator.next());

        while (iterator.hasNext()) {
            objectPath = objectPath.get(iterator.next());
        }

        return objectPath;
    }

    default Specification<T> propertyEqual(String path, final Object value) {
        return (root, cq, cb) -> {
            Path<?> pathToObject = this.resolvePathToObject(root, path);

            if (pathToObject.getJavaType().isEnum() && value instanceof String) {
                return cb.equal(pathToObject.as(String.class), value);
            } else if (Objects.isNull(value) || value instanceof String && value.equals("")) {
                return null;
            }
            return cb.equal(pathToObject, value);
        };
    }
}