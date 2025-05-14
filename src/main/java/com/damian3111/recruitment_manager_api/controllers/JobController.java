package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.JobRepository;
import com.damian3111.recruitment_manager_api.persistence.specification.CandidatesSpecification;
import com.damian3111.recruitment_manager_api.persistence.specification.JobSpecification;
import com.damian3111.recruitment_manager_api.services.JobService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openapitools.api.JobsApi;
import org.openapitools.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class JobController implements JobsApi {

    private final JobRepository jobRepository;
    private final JobService jobService;
    private final ModelMapper modelMapper;
    private final JobSpecification jobSpecification;

    @Override
    public ResponseEntity<List<JobDto>> getAllJobs() {

        List<JobEntity> all = jobRepository.findAll();
        List<JobDto> collect = all.stream().map(j -> modelMapper.map(j, JobDto.class)).toList();
        return ResponseEntity.ok(collect);
    }

    @Override
    public ResponseEntity<JobsPage> filterJobs(JobFilter jobFilter, Pageable pageable) {
        Page<JobEntity> entityPage = jobService.getCandidatesFiltered(
                this.getSpecification(jobFilter),
                pageable
        );
        JobsPage map = this.modelMapper.map(entityPage, JobsPage.class);
//        affiliationService.setAffiliationPositions(entityPage.getContent());
        return ResponseEntity.ok(map);    }

    @Override
    public ResponseEntity<List<JobDto>> getJobsByUserId(Long userId) {
        List<JobEntity> jobEntities = jobRepository.findByUserId(userId).orElseThrow();
        List<JobDto> parsedJobEntities = jobEntities.stream()
                .map(jobEntity -> modelMapper.map(jobEntity, JobDto.class)).collect(Collectors.toList());
        return ResponseEntity.ok(parsedJobEntities);
    }

    @Override
    public ResponseEntity<JobDto> createJob(JobDto jobDto) {
        JobEntity map = modelMapper.map(jobDto, JobEntity.class);
        map.setCompanyName("sdds");
        map.setPostedDate(LocalDate.now());
        map.setApplicationDeadline(LocalDate.now());
        JobEntity save = jobRepository.save(map);

        return ResponseEntity.ok(jobDto);
    }

    @Override
    public ResponseEntity<Void> deleteJob(Long id) {
        return JobsApi.super.deleteJob(id);
    }

    @Override
    public ResponseEntity<JobDto> getJobById(Long id) {
        return ResponseEntity.ok(modelMapper.map(jobRepository.findById(id).orElseThrow(), JobDto.class));
    }

    @Override
    public ResponseEntity<JobDto> updateJob(Long id, JobDto jobDto) {
        return JobsApi.super.updateJob(id, jobDto);
    }


    private Specification<JobEntity> getSpecification(JobFilter filter) {
        JobSpecification factory = this.jobSpecification;
//        CompanyEntity company = userEntityService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getCompany();
        Specification<JobEntity> jobSpec = null;
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

        return Optional.ofNullable(filter.getTitle())
                .map(v -> factory.propertyLikeIgnoreCase("title", v))
                .orElse(factory.empty())
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
                        .map(v -> factory.propertyInSkills(filter.getSkills())) // Use new skills specification
                        .orElse(factory.empty()));
    }
}
