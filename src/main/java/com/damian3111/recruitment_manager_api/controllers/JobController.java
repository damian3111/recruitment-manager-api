package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.JobRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openapitools.api.JobsApi;
import org.openapitools.model.JobDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
