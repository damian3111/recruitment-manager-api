package com.damian3111.recruitment_manager_api.persistence.repositories;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateEntity, Long>, JpaSpecificationExecutor<CandidateEntity> {
    Optional<CandidateEntity> findByEmail(String email);

    @Query("SELECT c FROM CandidateEntity c JOIN FETCH c.skills WHERE c.id = :id")
    Optional<CandidateEntity> findByIdWithSkills(@Param("id") Long id);

    @Query("""
    SELECT DISTINCT c FROM CandidateEntity c
    LEFT JOIN FETCH c.skills cs
    LEFT JOIN FETCH cs.skill
""")
    List<CandidateEntity> findAllWithSkillsAndSkill();
}

