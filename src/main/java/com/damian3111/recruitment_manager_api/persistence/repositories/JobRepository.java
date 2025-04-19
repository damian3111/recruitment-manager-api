package com.damian3111.recruitment_manager_api.persistence.repositories;

import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {

}
