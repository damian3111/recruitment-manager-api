package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.specification.CandidatesSpecification;
import com.damian3111.recruitment_manager_api.services.CandidateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.openapitools.api.CandidatesApi;
import org.openapitools.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CandidateController implements CandidatesApi {

    private final CandidateService candidateService;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<CandidateDto> addCandidate(CandidateDto candidateDto) {
        return ResponseEntity.status(201).body(modelMapper.map(candidateService.addCandidate(candidateDto), CandidateDto.class));
    }

    @Override
    public ResponseEntity<List<CandidateDto>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates().stream()
                .map(entity -> modelMapper.map(entity, CandidateDto.class))
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<CandidatesPage> filterCandidates(CandidateFilter filter, Pageable pageable) {
        return ResponseEntity.ok(modelMapper.map(
                candidateService.getCandidatesFiltered(filter, pageable), CandidatesPage.class
        ));
    }

    @Override
    public ResponseEntity<CandidateDto> getCandidateById(Long id) {
        try {
            CandidateEntity candidate = candidateService.getCandidateById(id);
            return ResponseEntity.ok(mapToCandidateDto(candidateService.getCandidateById(id)));
        } catch (EntityNotFoundException e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Override
    public ResponseEntity<CandidateDto> getCandidateByEmail(String email) {
        CandidateEntity candidateEntity = candidateService.getCandidateByEmail(email);
        CandidateDto candidateDto = modelMapper.map(candidateEntity, CandidateDto.class);
        candidateDto.setSkills(candidateEntity.getSkills().stream().map(s -> {
            CandidateDtoSkillsInner candidateDtoSkillsInner = new CandidateDtoSkillsInner();
            candidateDtoSkillsInner.setName(s.getSkill().getName());
            candidateDtoSkillsInner.setProficiencyLevel(CandidateDtoSkillsInner.ProficiencyLevelEnum.fromValue(s.getProficiencyLevel()));
            return candidateDtoSkillsInner;
        }).collect(Collectors.toList()));

        return ResponseEntity.ok(candidateDto);
    }

    @Override
    public ResponseEntity<CandidateDto> updateCandidate(Long id, CandidateDto candidateDto) {
        return ResponseEntity.ok(modelMapper.map(candidateService.updateCandidate(candidateDto), CandidateDto.class));
    }

    private CandidateDto mapToCandidateDto(CandidateEntity candidateEntity) {
        CandidateDto candidateDto = modelMapper.map(candidateEntity, CandidateDto.class);
        List<CandidateDtoSkillsInner> skills = candidateEntity.getSkills().stream()
                .map(s -> new CandidateDtoSkillsInner()
                        .name(s.getSkill().getName())
                        .proficiencyLevel(CandidateDtoSkillsInner.ProficiencyLevelEnum.fromValue(s.getProficiencyLevel())))
                .collect(Collectors.toList());
        candidateDto.setSkills(skills);
        return candidateDto;
    }

}
