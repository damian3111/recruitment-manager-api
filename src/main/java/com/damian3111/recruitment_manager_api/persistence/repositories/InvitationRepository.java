package com.damian3111.recruitment_manager_api.persistence.repositories;

import com.damian3111.recruitment_manager_api.persistence.entities.InvitationEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface InvitationRepository extends JpaRepository<InvitationEntity, Long> {

    Optional<List<InvitationEntity>> findByCandidate_IdAndRecruiter_Id(Long candidateId, Long recruiterId);
    Optional<List<InvitationEntity>> findByRecruiter_Id(Long recruiterId);

    @Query("SELECT i FROM InvitationEntity i WHERE i.job.user.id = :userId AND i.recruiter.id != :userId")
    Optional<List<InvitationEntity>> findByJobUserId(@Param("userId") Long userId);
}
