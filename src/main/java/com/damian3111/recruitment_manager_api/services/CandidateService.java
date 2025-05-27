package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkill;
import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.CandidateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public List<CandidateEntity> getAllCandidates() {
        return candidateRepository.findAll();
    }
//    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
//        return this.entityRepository.findAll(specification, pageable);
//    }

    public Page<CandidateEntity> getCandidatesFiltered(Specification<CandidateEntity> specification, Pageable pageable) {
        return candidateRepository.findAll(specification, pageable);
    }

    public Optional<CandidateEntity> getCandidateById(Long id) {
        return candidateRepository.findById(id);
    }

    public CandidateEntity createCandidate(CandidateEntity candidate) {
        return candidateRepository.save(candidate);
    }

    public CandidateEntity updateCandidate(Long id, CandidateEntity candidate, CandidateDto updated) {

            candidate.setPhone(updated.getPhone());
            candidate.setProfilePictureUrl(updated.getProfilePictureUrl());
            candidate.setHeadline(updated.getHeadline());
            candidate.setSummary(updated.getSummary());
            candidate.setExperience(updated.getExperience());
            candidate.setYearsOfExperience(updated.getYearsOfExperience());
            candidate.setEducation(updated.getEducation());
            candidate.setCertifications(updated.getCertifications());
            candidate.setWorkExperiences(updated.getWorkExperiences());
            candidate.setProjects(updated.getProjects());
            candidate.setMediaUrl(updated.getMediaUrl());
            candidate.setSalaryExpectation(updated.getSalaryExpectation());
            candidate.setWorkStyle(updated.getWorkStyle().toString());
            candidate.setAppliedDate(updated.getAppliedDate());
            candidate.setLocation(updated.getLocation());
            return candidateRepository.save(candidate);
    }

//
//    @Transactional
//    public CandidateEntity updateCandidate(Long id, CandidateEntity candidateEntity) {
//        if (!id.equals(candidateEntity.getId())) {
//            throw new IllegalArgumentException("ID mismatch");
//        }
//
//        CandidateEntity existing = candidateRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + id));
//
//        // Update basic fields
//        existing.setFirstName(candidateEntity.getFirstName());
//        existing.setLastName(candidateEntity.getLastName());
//        existing.setEmail(candidateEntity.getEmail());
//        existing.setPhone(candidateEntity.getPhone());
//        existing.setProfilePictureUrl(candidateEntity.getProfilePictureUrl());
//        existing.setHeadline(candidateEntity.getHeadline());
//        existing.setSummary(candidateEntity.getSummary());
//        existing.setExperience(candidateEntity.getExperience());
//        existing.setYearsOfExperience(candidateEntity.getYearsOfExperience());
//        existing.setEducation(candidateEntity.getEducation());
//        existing.setCertifications(candidateEntity.getCertifications());
//        existing.setWorkExperiences(candidateEntity.getWorkExperiences());
//        existing.setProjects(candidateEntity.getProjects());
//        existing.setMediaUrl(candidateEntity.getMediaUrl());
//        existing.setSalaryExpectation(candidateEntity.getSalaryExpectation());
//        existing.setWorkStyle(candidateEntity.getWorkStyle());
//        existing.setAppliedDate(candidateEntity.getAppliedDate());
//        existing.setLocation(candidateEntity.getLocation());
//
//        // Update skills in-place
//        existing.getSkills().clear(); // Clear existing skills
//        for (CandidateSkill skill : candidateEntity.getSkills()) {
//            skill.setCandidate(existing); // Ensure candidate reference
//            existing.getSkills().add(skill); // Add to existing collection
//        }
//
//        return candidateRepository.save(existing);
//    }
    public Optional<CandidateEntity> getCandidateByEmail(String email) {
        return candidateRepository.findByEmail(email);
    }
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }
}

