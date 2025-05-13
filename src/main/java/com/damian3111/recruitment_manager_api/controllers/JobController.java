package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.JobRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openapitools.api.JobsApi;
import org.openapitools.model.JobDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class JobController implements JobsApi {

    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<List<JobDto>> getAllJobs() {

        List<JobEntity> all = jobRepository.findAll();
        List<JobDto> collect = all.stream().map(j -> modelMapper.map(j, JobDto.class)).toList();
        return ResponseEntity.ok(collect);
    }

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
}
