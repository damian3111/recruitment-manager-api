package com.damian3111.recruitment_manager_api.persistence.repositories;


import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkill;
import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkillId;
import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, CandidateSkillId> {
    @Query("SELECT cs FROM CandidateSkill cs WHERE cs.candidate.id = :candidateId AND cs.skill.id = :skillId")
    Optional<CandidateSkill> findByCandidateIdAndSkillId(@Param("candidateId") Long candidateId,
                                                         @Param("skillId") Long skillId);

    @Modifying
    @Transactional
    @Query("UPDATE CandidateSkill cs SET cs.proficiencyLevel = :proficiencyLevel " +
            "WHERE cs.candidate.id = :candidateId AND cs.skill.id = :skillId")
    int updateProficiencyLevel(@Param("candidateId") Long candidateId,
                               @Param("skillId") Long skillId,
                               @Param("proficiencyLevel") String proficiencyLevel);
}
