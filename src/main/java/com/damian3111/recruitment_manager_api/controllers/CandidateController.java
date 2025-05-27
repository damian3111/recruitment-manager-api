package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkill;
import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateSkillRepository;
import com.damian3111.recruitment_manager_api.persistence.specification.CandidatesSpecification;
import com.damian3111.recruitment_manager_api.services.CandidateService;
import com.damian3111.recruitment_manager_api.services.SkillService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.openapitools.api.CandidatesApi;
import org.openapitools.model.CandidateDto;
import org.openapitools.model.CandidateFilter;
import org.openapitools.model.CandidatesPage;
import org.openapitools.model.JobDtoSkillsInner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class CandidateController implements CandidatesApi {

    private final CandidateService candidateService;
    private final ModelMapper modelMapper;
    private final CandidatesSpecification candidatesSpecification;
    private final SkillService skillService;
    private final CandidateRepository candidateRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    @Override
    public ResponseEntity<CandidateDto> addCandidate(CandidateDto candidateDto) {
        CandidateEntity entity = modelMapper.map(candidateDto, CandidateEntity.class);
        CandidateEntity savedEntity = candidateService.createCandidate(entity);
        CandidateDto savedDto = modelMapper.map(savedEntity, CandidateDto.class);
        return ResponseEntity.status(201).body(savedDto);
    }
//
    @Override
    public ResponseEntity<List<CandidateDto>> getAllCandidates() {
//    public ResponseEntity<List<CandidateDto>> getAllCandidates(CandidateFilter candidateFilter, Pageable pageable) {
        List<CandidateEntity> entities = candidateService.getAllCandidates();
        List<CandidateDto> dtos = entities.stream()
                .map(entity -> modelMapper.map(entity, CandidateDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);



//        Page<CandidateEntity> entityPage = this.candidateService.getAllCandidates(
//                this.getSpecification(candidateFilter),
//                pageable
//        );
//        affiliationService.setAffiliationPositions(entityPage.getContent());
//        return ResponseEntity.ok(this.modelMapper.map(entityPage, CandidatesPage.class));
    }

    @Override
    public ResponseEntity<CandidatesPage> filterCandidates(CandidateFilter candidateFilter, Pageable pageable) {
//        List<CandidateEntity> entities = candidateService.getAllCandidates();
//        List<CandidateDto> dtos = entities.stream()
//                .map(entity -> modelMapper.map(entity, CandidateDto.class))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(dtos);

        Page<CandidateEntity> entityPage = candidateService.getCandidatesFiltered(
                this.getSpecification(candidateFilter),
                pageable
        );
//        affiliationService.setAffiliationPositions(entityPage.getContent());
        return ResponseEntity.ok(this.modelMapper.map(entityPage, CandidatesPage.class));
    }


//    @Override
//    public ResponseEntity<CandidateDto> getCandidateById(Long id) {
//            return candidateService.getCandidateById(id)
//            .map(entity -> modelMapper.map(entity, CandidateDto.class))
//            .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//}

    @Override
    public ResponseEntity<CandidateDto> getCandidateById(Long id) {
        CandidateEntity candidateEntity = candidateService.getCandidateById(id).orElseThrow();
        CandidateDto candidateDto = modelMapper.map(candidateEntity, CandidateDto.class);
        candidateDto.setSkills(candidateEntity.getSkills().stream().map(s -> {
            JobDtoSkillsInner jobDtoSkillsInner = new JobDtoSkillsInner();
            jobDtoSkillsInner.setName(s.getSkill().getName());
            jobDtoSkillsInner.setProficiencyLevel(JobDtoSkillsInner.ProficiencyLevelEnum.fromValue(s.getProficiencyLevel()));
            return jobDtoSkillsInner;
        }).collect(Collectors.toList()));

        return ResponseEntity.ok(candidateDto);
}

    @Override
    public ResponseEntity<CandidateDto> getCandidateByEmail(String email) {
        CandidateEntity candidateEntity = candidateService.getCandidateByEmail(email).orElseThrow();
        CandidateDto candidateDto = modelMapper.map(candidateEntity, CandidateDto.class);
        candidateDto.setSkills(candidateEntity.getSkills().stream().map(s -> {
            JobDtoSkillsInner jobDtoSkillsInner = new JobDtoSkillsInner();
            jobDtoSkillsInner.setName(s.getSkill().getName());
            jobDtoSkillsInner.setProficiencyLevel(JobDtoSkillsInner.ProficiencyLevelEnum.fromValue(s.getProficiencyLevel()));
            return jobDtoSkillsInner;
        }).collect(Collectors.toList()));

        return ResponseEntity.ok(candidateDto);
    }

    @Override
    public ResponseEntity<CandidateDto> updateCandidate(Long id, CandidateDto candidateDto) {
        CandidateEntity candidateEntity = candidateService.getCandidateByEmail(candidateDto.getEmail()).orElseThrow();
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
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

//        CandidateSkill candidateSkill = candidateSkillRepository.save(new CandidateSkill(candidateEntity, java, "Good"));

        List<CandidateSkill> skills = candidateEntity.getSkills();
        skills.clear();
        skills.addAll(candidateSkills);

        return ResponseEntity.ok(modelMapper.map(candidateService.updateCandidate(candidateEntity.getId(), candidateEntity, candidateDto), CandidateDto.class));
    }

    private List<CandidateSkill> mapSkills(List<JobDtoSkillsInner> skillDtos, CandidateEntity candidate) {
        List<CandidateSkill> candidateSkills = new ArrayList<>();
        if (skillDtos == null) {
            return candidateSkills;
        }

        for (JobDtoSkillsInner skillDto : skillDtos) {
            if (skillDto.getName() == null) {
                continue; // Skip invalid skills
            }

            // Find or create SkillEntity
            SkillEntity skillEntity = skillService.findOrCreateSkill(skillDto.getName());

            // Get proficiency level
            String proficiencyLevel = skillDto.getProficiencyLevel() != null
                    ? skillDto.getProficiencyLevel().getValue()
                    : "Beginner"; // Default if null

            // Validate proficiency level
            if (!List.of("Beginner", "Familiar", "Good", "Expert").contains(proficiencyLevel)) {
                throw new IllegalArgumentException("Invalid proficiency level: " + proficiencyLevel);
            }

            // Create CandidateSkill
            CandidateSkill candidateSkill = CandidateSkill.builder()
                    .candidate(candidate)
                    .skill(skillEntity)
                    .proficiencyLevel(proficiencyLevel)
                    .build();

            candidateSkills.add(candidateSkill);
        }
        return candidateSkills;
    }

    private Specification<CandidateEntity> getSpecification(CandidateFilter filter) {
        CandidatesSpecification factory = this.candidatesSpecification;
//        CompanyEntity company = userEntityService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getCompany();
        Specification<CandidateEntity> candidateSpec = null;
//        if (company != null) {
//            companySpec = (root, cq, cb) -> {
//                cq.multiselect(root);
//                cq.orderBy(
//                        cb.desc(
//                                cb.selectCase()
//                                        .when(cb.equal(root.get("company").get("id"), company.getId()), 1)
//                                        .otherwise(0)
//                        ),
//                        cb.desc(root.get("id"))
//                );
//                return null;
//            };
//        }
//        if(company != null) {
//            List<CompanyEntity> companies = new ArrayList<>();
//            companies.add(company);
//            companies.addAll(company.getContractors());
//            companySpec = factory.propertyIn("company", companies);
//        }

        return  Optional.ofNullable(filter.getFirstName())
                                .map(v -> factory.propertyLikeIgnoreCase("firstName", v))
                                .orElse(factory.empty())
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
                        .map(v -> factory.propertyInSkills(filter.getSkills())) // Use new skills specification
                        .orElse(factory.empty()));
    }
}
