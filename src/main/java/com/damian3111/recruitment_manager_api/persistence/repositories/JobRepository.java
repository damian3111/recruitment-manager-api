package com.damian3111.recruitment_manager_api.persistence.repositories;

import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long>, JpaSpecificationExecutor<JobEntity> {
    Optional<List<JobEntity>> findByUserId(Long id);
}
