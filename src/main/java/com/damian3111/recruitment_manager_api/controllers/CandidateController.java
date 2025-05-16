package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.specification.CandidatesSpecification;
import com.damian3111.recruitment_manager_api.services.CandidateService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.openapitools.api.CandidatesApi;
import org.openapitools.model.CandidateDto;
import org.openapitools.model.CandidateFilter;
import org.openapitools.model.CandidatesPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class CandidateController implements CandidatesApi {

    private final CandidateService candidateService;
    private final ModelMapper modelMapper;
    private final CandidatesSpecification candidatesSpecification;

    @Override
    public ResponseEntity<CandidateDto> addCandidate(CandidateDto candidateDto) {
        CandidateEntity entity = modelMapper.map(candidateDto, CandidateEntity.class);
        CandidateEntity savedEntity = candidateService.createCandidate(entity);
        CandidateDto savedDto = modelMapper.map(savedEntity, CandidateDto.class);
        return ResponseEntity.status(201).body(savedDto);
    }
//
    @Override
    public ResponseEntity<List<CandidateDto>> getAllCandidates(CandidateFilter candidateFilter) {
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


    @Override
    public ResponseEntity<CandidateDto> getCandidateById(Long id) {
            return candidateService.getCandidateById(id)
            .map(entity -> modelMapper.map(entity, CandidateDto.class))
            .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
}

    @Override
    public ResponseEntity<CandidateDto> getCandidateByEmail(String email) {
        return candidateService.getCandidateByEmail(email)
                .map(entity -> modelMapper.map(entity, CandidateDto.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(null));
    }

    @Override
    public ResponseEntity<CandidateDto> updateCandidate(Long id, CandidateDto candidateDto) {
        return ResponseEntity.ok(modelMapper.map(candidateService.updateCandidate(id, modelMapper.map(candidateDto, CandidateEntity.class)), CandidateDto.class));
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
                .and(Optional.ofNullable(filter.getSalaryExpectation())
                        .map(v -> factory.propertyLikeIgnoreCase("salaryExpectations", v))
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
                        .orElse(factory.empty()));
    }

}
