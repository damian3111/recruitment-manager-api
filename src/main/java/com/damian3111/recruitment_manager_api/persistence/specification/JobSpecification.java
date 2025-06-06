package com.damian3111.recruitment_manager_api.persistence.specification;

import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.JobSkill;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.openapitools.model.JobDtoSkillsInner;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobSpecification implements BaseSpecification<JobEntity, Long>{

    public Specification<JobEntity> propertyInSkills(List<JobDtoSkillsInner> skills) {
        return (root, cq, cb) -> {
            if (skills == null || skills.isEmpty()) {
                return cb.conjunction();
            }

            Join<JobEntity, JobSkill> jobSkillJoin = root.join("skills");
            Join<JobSkill, JobEntity> skillJoin = jobSkillJoin.join("skill");

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
                                            jobSkillJoin.get("proficiencyLevel"),
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
}
