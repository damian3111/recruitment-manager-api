package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.*;
import com.damian3111.recruitment_manager_api.persistence.repositories.JobRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.JobSkillRepository;
import com.damian3111.recruitment_manager_api.persistence.specification.JobSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openapitools.model.JobDto;
import org.openapitools.model.JobDtoSkillsInner;
import org.openapitools.model.JobFilter;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;
    private final JobSpecification jobSpecification;
    private final JobSkillRepository jobSkillRepository;
    private final SkillService skillService;
    private final UserService userService;

    private static final List<String> VALID_PROFICIENCY_LEVELS = List.of("Beginner", "Familiar", "Good", "Expert");

    public Page<JobEntity> getCandidatesFiltered(JobFilter jobFilter, Pageable pageable) {
        return jobRepository.findAll(getSpecification(jobFilter), pageable);
    }

    public List<JobEntity> getAllJobs() {
        return jobRepository.findAll();
    }

    public List<JobEntity> getJobsByUserId(Long userId) {
        return jobRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("No jobs found for user ID: " + userId));
    }

    public JobEntity getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job not found with ID: " + id));
    }


    public JobEntity createJob(JobDto jobDto) {
        //TEMPORARY CONFIG
        JobEntity map = modelMapper.map(jobDto, JobEntity.class);
        map.setCompanyName("sdds");
        map.setPostedDate(LocalDate.now());
        map.setApplicationDeadline(LocalDate.now());

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userService.getUserByEmail(name);

        ArrayList<ArrayList> skillEntities = new ArrayList<>();
        map.setUser(userEntity);
        map.setSkills(new ArrayList<>());
        JobEntity jobEntity = jobRepository.save(map);

        jobDto.getSkills().forEach(s -> {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add(skillService.findByName(s.getName()));
            objects.add(s.getProficiencyLevel().getValue());
            skillEntities.add(objects);
        });

        ArrayList<JobSkill> jobSkills = new ArrayList<>();

        skillEntities.forEach(s -> {
            SkillEntity skill = (SkillEntity) s.get(0);
            String proficiencyLevel = (String) s.get(1);
            JobSkill newSkill = JobSkill.builder()
                    .skill(skill)
                    .job(jobEntity)
                    .proficiencyLevel(proficiencyLevel)
                    .build();
            jobSkillRepository.save(newSkill);
            jobSkills.add(newSkill);
        });

        List<JobSkill> skills = jobEntity.getSkills();
        skills.clear();
        skills.addAll(jobSkills);

        return jobEntity;
    }

    public JobDto updateJob(Long id, JobDto jobDto) {
        JobEntity jobEntity = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job not found with ID: " + id));

        modelMapper.map(jobDto, jobEntity);
        List<JobSkill> newSkills = mapSkills(jobDto.getSkills(), jobEntity);
        updateJobSkills(jobEntity, newSkills);

        JobEntity updatedEntity = jobRepository.save(jobEntity);
        return mapToJobDto(updatedEntity);
    }

    private List<JobSkill> mapSkills(List<JobDtoSkillsInner> skillDtos, JobEntity job) {
        if (skillDtos == null || skillDtos.isEmpty()) {
            return List.of();
        }

        return skillDtos.stream()
                .filter(skillDto -> skillDto.getName() != null)
                .map(skillDto -> {
                    SkillEntity skillEntity = skillService.findOrCreateSkill(skillDto.getName());
                    String proficiencyLevel = Optional.ofNullable(skillDto.getProficiencyLevel())
                            .map(JobDtoSkillsInner.ProficiencyLevelEnum::getValue)
                            .orElse("Beginner");

                    if (!VALID_PROFICIENCY_LEVELS.contains(proficiencyLevel)) {
                        throw new IllegalArgumentException("Invalid proficiency level: " + proficiencyLevel);
                    }

                    return JobSkill.builder()
                            .job(job)
                            .skill(skillEntity)
                            .proficiencyLevel(proficiencyLevel)
                            .build();
                })
                .collect(Collectors.toList());
    }


    private JobDto mapToJobDto(JobEntity jobEntity) {
        JobDto jobDto = modelMapper.map(jobEntity, JobDto.class);
        List<JobDtoSkillsInner> skills = jobEntity.getSkills().stream()
                .map(s -> new JobDtoSkillsInner()
                        .name(s.getSkill().getName())
                        .proficiencyLevel(JobDtoSkillsInner.ProficiencyLevelEnum.fromValue(s.getProficiencyLevel())))
                .collect(Collectors.toList());
        jobDto.setSkills(skills);
        return jobDto;
    }

    private void updateJobSkills(JobEntity job, List<JobSkill> newSkills) {
    }

    private Specification<JobEntity> getSpecification(JobFilter filter) {
        JobSpecification factory = this.jobSpecification;
        return Specification.where(Optional.ofNullable(filter.getTitle())
                        .map(v -> factory.propertyLikeIgnoreCase("title", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getEmploymentType())
                        .map(v -> factory.propertyLikeIgnoreCase("employmentType", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getLocation())
                        .map(v -> factory.propertyLikeIgnoreCase("location", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getSalaryMin())
                        .map(v -> factory.propertyGreaterOrEqual("salaryMin", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getSalaryMax())
                        .map(v -> factory.propertyLessOrEqual("salaryMax", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getCurrency())
                        .map(v -> factory.propertyLikeIgnoreCase("currency", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getExperienceLevel())
                        .map(v -> factory.propertyLikeIgnoreCase("experienceLevel", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getIndustry())
                        .map(v -> factory.propertyLikeIgnoreCase("industry", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getCompanyName())
                        .map(v -> factory.propertyLikeIgnoreCase("companyName", v))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getEmploymentMode())
                        .map(v -> factory.propertyLikeIgnoreCase("employmentMode", v.toString()))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getPostedDate())
                        .map(v -> factory.propertyLikeIgnoreCase("postedDate", v.toString()))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getApplicationDeadline())
                        .map(v -> factory.propertyLikeIgnoreCase("applicationDeadline", v.toString()))
                        .orElse(factory.empty()))
                .and(Optional.ofNullable(filter.getSkills())
                        .map(factory::propertyInSkills)
                        .orElse(factory.empty()));
    }

    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new EntityNotFoundException("Job not found with ID: " + id);
        }
        jobRepository.deleteById(id);
    }
}
