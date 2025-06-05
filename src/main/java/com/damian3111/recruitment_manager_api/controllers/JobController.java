package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.*;
import com.damian3111.recruitment_manager_api.persistence.repositories.JobRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.JobSkillRepository;
import com.damian3111.recruitment_manager_api.persistence.specification.CandidatesSpecification;
import com.damian3111.recruitment_manager_api.persistence.specification.JobSpecification;
import com.damian3111.recruitment_manager_api.services.JobService;
import com.damian3111.recruitment_manager_api.services.SkillService;
import com.damian3111.recruitment_manager_api.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.openapitools.api.JobsApi;
import org.openapitools.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class JobController implements JobsApi {

    private final JobRepository jobRepository;
    private final JobService jobService;
    private final ModelMapper modelMapper;
    private final JobSpecification jobSpecification;
    private final JobSkillRepository jobSkillRepository;
    private final SkillService skillService;
    private final UserService userService;

    private static final List<String> VALID_PROFICIENCY_LEVELS = List.of("Beginner", "Familiar", "Good", "Expert");

    @Override
    public ResponseEntity<List<JobDto>> getAllJobs() {
        return ResponseEntity.ok(jobRepository.findAll().stream()
                .map(this::mapToJobDto)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<JobsPage> filterJobs(JobFilter filter, Pageable pageable) {
        return ResponseEntity.ok(modelMapper.map(
                jobService.getCandidatesFiltered(filter, pageable), JobsPage.class)
        );
    }

    @Override
    public ResponseEntity<List<JobDto>> getJobsByUserId(Long userId) {
        return ResponseEntity.ok(jobService.getJobsByUserId(userId).stream()
                .map(this::mapToJobDto)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<JobDto> createJob(JobDto jobDto) {
        return ResponseEntity.ok(modelMapper.map(jobService.createJob(jobDto), JobDto.class));
    }

    @Override
    public ResponseEntity<Void> deleteJob(Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
    @Override
    public ResponseEntity<JobDto> getJobById(Long id) {
        return ResponseEntity.ok(mapToJobDto(jobService.getJobById(id)));
    }

    @Override
    public ResponseEntity<JobDto> updateJob(Long id, @Valid JobDto jobDto) {
        return ResponseEntity.ok(jobService.updateJob(id, jobDto));
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
}
