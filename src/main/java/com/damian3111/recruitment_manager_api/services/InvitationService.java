package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.*;
import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.InvitationRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.JobRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.InvitationDto;
import org.openapitools.model.InvitationStatus;
import org.openapitools.model.UpdateInvitationStatusRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    public InvitationEntity createInvitation(InvitationDto dto) {
        InvitationEntity invitation = new InvitationEntity();

        UserEntity recruiter = userRepository.findById(dto.getRecruiterId())
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));
        invitation.setRecruiter(recruiter);

        CandidateEntity candidate = candidateRepository.findById(dto.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        invitation.setCandidate(candidate);

        JobEntity job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Optional<List<InvitationEntity>> byCandidateEmailOrJobUserEmail2 = invitationRepository.findByCandidateEmailOrJobUserEmail2(candidate.getId(), job.getId());
        if (!byCandidateEmailOrJobUserEmail2.get().isEmpty())
            throw new RuntimeException("Invitation already exissts");

        invitation.setJob(job);

        invitation.setStatus(dto.getStatus());
        invitation.setCreatedAt(LocalDateTime.now());

        return invitationRepository.save(invitation);
    }

    public List<InvitationEntity> getAllInvitations() {
        return invitationRepository.findAll();
    }

    public Optional<InvitationEntity> getInvitationById(Long id) {
        return invitationRepository.findById(id);
    }

    public List<InvitationEntity> getUserRelatedInvitations(Long userId, String email) {
        return invitationRepository.findByCandidateEmailOrJobUserEmail(userId, email).orElseThrow();
    }

    public Optional<List<InvitationEntity>> getInvitationsByCandidateAndRecruiter(Long candidateId, Long recruiterId) {

        return invitationRepository.findByCandidate_IdAndRecruiter_Id(candidateId, recruiterId);
    }

    public Optional<List<InvitationEntity>> getInvitationsByRecruiter(Long recruiterId) {

        return invitationRepository.findByRecruiter_Id(recruiterId);
    }

    private Optional<List<InvitationEntity>> getInvitationsReceivedByRecruiter(Long recruiterId, String email) {
        //
        return invitationRepository.findByJobUserId(recruiterId, email);
    }

    private Optional<List<InvitationEntity>> getInvitationsReceivedByRecruited(Long recruiterId, String email) {
        //up
        return invitationRepository.findByCandidate(recruiterId, email);
    }

    public Optional<List<InvitationEntity>> getInvitationsReceivedByRecruited2(Long recruiterId, String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean recruited = authentication.getAuthorities().stream().anyMatch(p -> p.getAuthority().equals("RECRUITED"));
        if (recruited){
            return getInvitationsReceivedByRecruited(recruiterId, email);
        }else {
            return invitationRepository.findByJobUserId(recruiterId, email);
        }
    }
    public Optional<List<InvitationEntity>> getAcceptedInvitations(Long recruiterId, String email) {
        //up
        return invitationRepository.findByCandidate2(recruiterId, email, InvitationStatus.ACCEPTED);
    }

    @Transactional
    public InvitationEntity updateInvitationStatusById(Long invitationId, UpdateInvitationStatusRequest updateInvitationStatusRequest) {
        int updated = invitationRepository.updateStatusById(
                invitationId,
                updateInvitationStatusRequest.getStatus()
        );

        if (updated > 0) {
            return invitationRepository.findById(invitationId).orElse(null);
        } else {
            throw new EntityNotFoundException("Invitation not found or not updated");
        }
    }

    public void deleteInvitation(Long id) {
        invitationRepository.deleteById(id);
    }
}

