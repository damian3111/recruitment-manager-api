package com.damian3111.recruitment_manager_api.persistence.repositories;


import com.damian3111.recruitment_manager_api.persistence.entities.JobSkill;
import com.damian3111.recruitment_manager_api.persistence.entities.JobSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
}
