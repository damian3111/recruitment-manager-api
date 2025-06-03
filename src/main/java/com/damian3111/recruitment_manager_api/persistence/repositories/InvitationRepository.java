package com.damian3111.recruitment_manager_api.persistence.repositories;

import com.damian3111.recruitment_manager_api.persistence.entities.InvitationEntity;
import jakarta.transaction.Transactional;
import org.openapitools.model.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface InvitationRepository extends JpaRepository<InvitationEntity, Long> {

    Optional<List<InvitationEntity>> findByCandidate_IdAndRecruiter_Id(Long candidateId, Long recruiterId);
    Optional<List<InvitationEntity>> findByRecruiter_Id(Long recruiterId);
    @Query("SELECT i FROM InvitationEntity i WHERE i.job.user.email = :email AND i.recruiter.id != :userId")
    Optional<List<InvitationEntity>> findByJobUserId(@Param("userId") Long userId, @Param("email") String email);
    @Query("SELECT i FROM InvitationEntity i WHERE i.candidate.email = :email AND i.recruiter.id != :userId")
    Optional<List<InvitationEntity>> findByCandidate(@Param("userId") Long userId, @Param("email") String email);
    @Query("SELECT i FROM InvitationEntity i WHERE (i.candidate.email = :email OR i.job.user.email = :email) AND i.status = :status")
    Optional<List<InvitationEntity>> findByCandidate2(@Param("userId") Long userId, @Param("email") String email, InvitationStatus status);
    @Query("SELECT i FROM InvitationEntity i WHERE i.candidate.email = :email OR i.job.user.email = :email")
    Optional<List<InvitationEntity>> findByCandidateEmailOrJobUserEmail(@Param("userId") Long userId, @Param("email") String email);
    @Query("SELECT i FROM InvitationEntity i WHERE (i.candidate.id = :candidateId AND i.job.id = :jobId) ")
    Optional<List<InvitationEntity>> findByCandidateEmailOrJobUserEmail2(@Param("candidateId") Long candidateId, @Param("jobId") Long jobId);
    @Modifying
    @Transactional
    @Query("UPDATE InvitationEntity i SET i.status = :status WHERE i.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") InvitationStatus status);
}


