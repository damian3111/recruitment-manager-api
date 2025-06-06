package com.damian3111.recruitment_manager_api.persistence.specification;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkill;
import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
import jakarta.persistence.criteria.*;
import org.openapitools.model.CandidateDtoSkillsInner;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CandidatesSpecification implements BaseSpecification<CandidateEntity, Long>{

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

    public Specification<CandidateEntity> propertyInSkills(List<CandidateDtoSkillsInner> skills) {
        return (root, cq, cb) -> {
            if (skills == null || skills.isEmpty()) {
                return cb.conjunction();
            }

            Join<CandidateEntity, CandidateSkill> candidateSkillJoin = root.join("skills");
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
                                            skill.getProficiencyLevel().toString()
                                    )
                            );
                        }
                        return namePredicate;
                    })
                    .collect(Collectors.toList());

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
