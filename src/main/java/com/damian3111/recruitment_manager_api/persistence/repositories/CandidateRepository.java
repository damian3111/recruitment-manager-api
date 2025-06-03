package com.damian3111.recruitment_manager_api.persistence.repositories;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateEntity, Long>, JpaSpecificationExecutor<CandidateEntity> {

    Optional<CandidateEntity> findByEmail(String email);
}

