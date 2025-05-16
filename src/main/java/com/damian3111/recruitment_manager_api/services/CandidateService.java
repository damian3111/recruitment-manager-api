package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateRepository;
import lombok.RequiredArgsConstructor;
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

    public CandidateEntity updateCandidate(Long id, CandidateEntity updated) {
        return candidateRepository.findById(id).map(candidate -> {
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
            candidate.setWorkStyle(updated.getWorkStyle());
            candidate.setAppliedDate(updated.getAppliedDate());
            candidate.setLocation(updated.getSkills());
            candidate.setSkills(updated.getSkills());
            return candidateRepository.save(candidate);
        }).orElseThrow(() -> new RuntimeException("Candidate not found"));
    }
    public Optional<CandidateEntity> getCandidateByEmail(String email) {
        return candidateRepository.findByEmail(email);
    }
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }
}

