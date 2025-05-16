package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.InvitationEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.InvitationRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.JobRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.InvitationDto;
import org.openapitools.model.InvitationStatus;
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

    public Optional<List<InvitationEntity>> getInvitationsByCandidateAndRecruiter(Long candidateId, Long recruiterId) {

        return invitationRepository.findByCandidate_IdAndRecruiter_Id(candidateId, recruiterId);
    }

    public Optional<List<InvitationEntity>> getInvitationsByRecruiter(Long recruiterId) {

        return invitationRepository.findByRecruiter_Id(recruiterId);
    }

    public Optional<List<InvitationEntity>> getInvitationsReceivedByRecruiter(Long recruiterId) {


        return invitationRepository.findByJobUserId(recruiterId);
    }


    public void deleteInvitation(Long id) {
        invitationRepository.deleteById(id);
    }
}

