package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkill;
import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateSkillRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.SkillRepository;
import com.damian3111.recruitment_manager_api.persistence.specification.CandidatesSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openapitools.model.CandidateDto;
import org.openapitools.model.CandidateFilter;
import org.openapitools.model.JobDtoSkillsInner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final SkillService skillService;
    private final ModelMapper modelMapper;
    private final CandidatesSpecification candidatesSpecification;

    @Cacheable(value = "candidatesList", key = "'allCandidates'")
    public List<CandidateEntity> getAllCandidates() {
        return candidateRepository.findAll();
    }
//    @Cacheable(value = "candidatesPage", key = "'sdsdsd'")
    public Page<CandidateEntity> getCandidatesFiltered(CandidateFilter filter, Pageable pageable) {
        return candidateRepository.findAll(getSpecification(filter), pageable);
    }
    @Cacheable(value = "candidates", key = "#id")
    public CandidateEntity getCandidateById(Long id) {
        return candidateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Candidate not found with ID: " + id));
    }
    @Cacheable(value = "candidates", key = "#email")
    public CandidateEntity getCandidateByEmail(String email) {
        return candidateRepository.findByEmail(email).orElseThrow();
    }

    @Transactional
    @CachePut(value = "candidates", key = "#result.email")
    @CacheEvict(value = {"candidatesList", "candidatesPage"}, allEntries = true)
    public CandidateEntity addCandidate(CandidateDto candidateDto) {
        CandidateEntity candidateEntity = modelMapper.map(candidateDto, CandidateEntity.class);
        ArrayList<ArrayList<?>> skillEntities = new ArrayList<>();
        candidateEntity.setSkills(new ArrayList<>());
        candidateRepository.save(candidateEntity);

        candidateDto.getSkills().forEach(s -> {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add(skillService.findByName(s.getName()));
            objects.add(s.getProficiencyLevel().getValue());
            skillEntities.add(objects);
        });

        ArrayList<CandidateSkill> candidateSkills = new ArrayList<>();

        skillEntities.forEach(s -> {
            SkillEntity skill = (SkillEntity) s.get(0);
            String proficiencyLevel = (String) s.get(1);
            Optional<CandidateSkill> byCandidateIdAndSkillId = candidateSkillRepository.findByCandidateIdAndSkillId(candidateEntity.getId(), skill.getId());
            if (byCandidateIdAndSkillId.isPresent()){
                candidateSkillRepository.updateProficiencyLevel(candidateEntity.getId(), skill.getId(), proficiencyLevel);
            }else {
                CandidateSkill newSkill = CandidateSkill.builder()
                        .skill(skill)
                        .candidate(candidateEntity)
                        .proficiencyLevel(proficiencyLevel)
                        .build();
                candidateSkillRepository.save(newSkill);
            }
            CandidateSkill candidateSkill = candidateSkillRepository.findByCandidateIdAndSkillId(candidateEntity.getId(), skill.getId()).orElseThrow();
            candidateSkills.add(candidateSkill);
        });

        List<CandidateSkill> skills = candidateEntity.getSkills();
        skills.clear();
        skills.addAll(candidateSkills);


        return candidateRepository.save(candidateEntity);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "candidates", key = "#candidateEntity.id"),
                    @CacheEvict(value = {"candidatesList", "candidatesPage"}, allEntries = true)
            },
            put = {
                    @CachePut(value = "candidates", key = "#candidateDto.email")
            }
    )
    public CandidateEntity updateCandidate(CandidateDto candidateDto) {
        CandidateEntity candidateEntity = getCandidateByEmail(candidateDto.getEmail());
        ArrayList<ArrayList> skillEntities = new ArrayList<>();

        candidateDto.getSkills().forEach(s -> {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add(skillService.findByName(s.getName()));
            objects.add(s.getProficiencyLevel().getValue());
            skillEntities.add(objects);
        });

        ArrayList<CandidateSkill> candidateSkills = new ArrayList<>();

        skillEntities.forEach(s -> {
            SkillEntity skill = (SkillEntity) s.get(0);
            String proficiencyLevel = (String) s.get(1);
            Optional<CandidateSkill> byCandidateIdAndSkillId = candidateSkillRepository.findByCandidateIdAndSkillId(candidateEntity.getId(), skill.getId());
            if (byCandidateIdAndSkillId.isPresent()){
                candidateSkillRepository.updateProficiencyLevel(candidateEntity.getId(), skill.getId(), proficiencyLevel);
            }else {
                CandidateSkill newSkill = CandidateSkill.builder()
                        .skill(skill)
                        .candidate(candidateEntity)
                        .proficiencyLevel(proficiencyLevel)
                        .build();
                candidateSkillRepository.save(newSkill);
            }
            CandidateSkill candidateSkill = candidateSkillRepository.findByCandidateIdAndSkillId(candidateEntity.getId(), skill.getId()).orElseThrow();
            candidateSkills.add(candidateSkill);
        });

        List<CandidateSkill> skills = candidateEntity.getSkills();
        skills.clear();
        skills.addAll(candidateSkills);

        return updateCandidate(candidateEntity, candidateDto);
    }

    @Transactional
    public CandidateEntity updateCandidate(CandidateEntity candidate, CandidateDto updated) {

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
//    public CandidateEntity updateCandidate(Long id, CandidateDto candidateEntity) {
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
//        existing.setWorkStyle(candidateEntity.getWorkStyle().getValue());
//        existing.setAppliedDate(candidateEntity.getAppliedDate());
//        existing.setLocation(candidateEntity.getLocation());
//
//        // Update skills in-place
//        existing.getSkills().clear(); // Clear existing skills
//        for (CandidateSkill skill : candidateEntity.getSkills()) {
//            skillRepository.findByName(s.)
//            skill.setCandidate(existing); // Ensure candidate reference
//            existing.getSkills().add(skill); // Add to existing collection
//        }
//        candidateSkillRepository.saveAll(existing.getSkills());
//        return candidateRepository.save(existing);
//    }

    @Transactional
    @CacheEvict(value = {"candidates", "candidatesList", "candidatesPage"}, allEntries = true)
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }


    private Specification<CandidateEntity> getSpecification(CandidateFilter filter) {
        CandidatesSpecification factory = this.candidatesSpecification;
        return Specification.where(Optional.ofNullable(filter.getFirstName())
                        .map(v -> factory.propertyLikeIgnoreCase("firstName", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getLastName())
                        .map(v -> factory.propertyLikeIgnoreCase("lastName", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getEmail())
                        .map(v -> factory.propertyLikeIgnoreCase("email", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getPhone())
                        .map(v -> factory.propertyLikeIgnoreCase("phone", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getHeadline())
                        .map(v -> factory.propertyLikeIgnoreCase("headline", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getMinExperience())
                        .map(v -> factory.propertyGreaterOrEqual("yearsOfExperience", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getMaxExperience())
                        .map(v -> factory.propertyLessOrEqual("yearsOfExperience", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getEducation())
                        .map(v -> factory.propertyLikeIgnoreCase("education", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getCertifications())
                        .map(v -> factory.propertyLikeIgnoreCase("certifications", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getProjects())
                        .map(v -> factory.propertyLikeIgnoreCase("projects", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getSalaryMin())
                        .map(v -> factory.propertyGreaterOrEqual("salaryExpectation", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getSalaryMax())
                        .map(v -> factory.propertyLessOrEqual("salaryExpectation", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getWorkStyle())
                        .map(v -> factory.propertyLikeIgnoreCase("workStyle", v.getValue()))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getAppliedDateFrom())
                        .map(v -> factory.afterAppliedDate("appliedDate", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getAppliedDateTo())
                        .map(v -> factory.beforeAppliedDate("appliedDate", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getLocation())
                        .map(v -> factory.propertyLikeIgnoreCase("location", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getSkills())
                        .map(factory::propertyInSkills)
                        .orElse(factory.empty()));
    }
}

