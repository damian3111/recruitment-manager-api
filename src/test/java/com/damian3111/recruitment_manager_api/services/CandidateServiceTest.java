//package com.damian3111.recruitment_manager_api.services;
//
//import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
//import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkill;
//import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
//import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateRepository;
//import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateSkillRepository;
//import com.damian3111.recruitment_manager_api.persistence.specification.CandidatesSpecification;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//import org.openapitools.model.CandidateDto;
//import org.openapitools.model.CandidateDtoSkillsInner;
//import org.openapitools.model.CandidateFilter;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.lang.reflect.Method;
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CandidateServiceTest {
//
//    @Mock
//    private CandidateRepository candidateRepository;
//
//    @Mock
//    private CandidateSkillRepository candidateSkillRepository;
//
//    @Mock
//    private SkillService skillService;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @Mock
//    private CandidatesSpecification candidatesSpecification;
//
//    @InjectMocks
//    private CandidateService candidateService;
//
//    private CandidateEntity candidateEntity;
//    private CandidateDto candidateDto;
//    private SkillEntity skillEntity;
//    private CandidateSkill candidateSkill;
//    private CandidateDtoSkillsInner skillDto;
//    private CandidateFilter candidateFilter;
//    private final Long candidateId = 1L;
//    private final String candidateEmail = "john.doe@example.com";
//    private final Long skillId = 1L;
//    private final String skillName = "Java";
//    private final String proficiencyLevel = "Expert";
//
//    @BeforeEach
//    void setUp() {
//        candidateEntity = new CandidateEntity();
//        candidateEntity.setId(candidateId);
//        candidateEntity.setFirstName("John");
//        candidateEntity.setLastName("Doe");
//        candidateEntity.setEmail(candidateEmail);
//        candidateEntity.setPhone("1234567890");
//        candidateEntity.setProfilePictureUrl("https://example.com/profile.jpg");
//        candidateEntity.setHeadline("Software Engineer");
//        candidateEntity.setSummary("Experienced developer");
//        candidateEntity.setExperience("5 years in Java");
//        candidateEntity.setYearsOfExperience(5);
//        candidateEntity.setEducation("BSc Computer Science");
//        candidateEntity.setCertifications("AWS Certified");
//        candidateEntity.setWorkExperiences("Worked at XYZ Corp");
//        candidateEntity.setProjects("Built a microservices app");
//        candidateEntity.setMediaUrl("https://example.com/portfolio");
//        candidateEntity.setSalaryExpectation("100000 USD");
//        candidateEntity.setWorkStyle("REMOTE");
//        candidateEntity.setAppliedDate(LocalDate.of(2025, 6, 1));
//        candidateEntity.setLocation("New York");
//        candidateEntity.setSkills(new ArrayList<>());
//
//        candidateDto = new CandidateDto();
//        candidateDto.setId(1);
//        candidateDto.setFirstName("John");
//        candidateDto.setLastName("Doe");
//        candidateDto.setEmail(candidateEmail);
//        candidateDto.setPhone("1234567890");
//        candidateDto.setProfilePictureUrl("https://example.com/profile.jpg");
//        candidateDto.setHeadline("Software Engineer");
//        candidateDto.setSummary("Experienced developer");
//        candidateDto.setExperience("5 years in Java");
//        candidateDto.setYearsOfExperience(5);
//        candidateDto.setEducation("BSc Computer Science");
//        candidateDto.setCertifications("AWS Certified");
//        candidateDto.setWorkExperiences("Worked at XYZ Corp");
//        candidateDto.setProjects("Built a microservices app");
//        candidateDto.setMediaUrl("https://example.com/portfolio");
//        candidateDto.setSalaryExpectation("100000 USD");
//        candidateDto.setWorkStyle(CandidateDto.WorkStyleEnum.REMOTE);
//        candidateDto.setAppliedDate(LocalDate.of(2025, 6, 1));
//        candidateDto.setLocation("New York");
//        candidateDto.setSkills(new ArrayList<>());
//
//        skillEntity = new SkillEntity();
//        skillEntity.setId(skillId);
//        skillEntity.setName(skillName);
//        skillEntity.setCreatedAt(LocalDate.of(2025, 6, 1));
//
//        candidateSkill = CandidateSkill.builder()
//                .candidate(candidateEntity)
//                .skill(skillEntity)
//                .proficiencyLevel(proficiencyLevel)
//                .build();
//
//        skillDto = new CandidateDtoSkillsInner();
//        skillDto.setName(skillName);
//        skillDto.setProficiencyLevel(CandidateDtoSkillsInner.ProficiencyLevelEnum.EXPERT);
//
//        candidateFilter = new CandidateFilter();
//        candidateFilter.setFirstName("John");
//        candidateFilter.setSkills(Collections.singletonList(skillDto));
//    }
//
//    @Test
//    void getAllCandidates_ReturnsAllCandidates() {
//        // Given
//        List<CandidateEntity> candidates = Collections.singletonList(candidateEntity);
//        when(candidateRepository.findAll()).thenReturn(candidates);
//
//        // When
//        List<CandidateEntity> result = candidateService.getAllCandidates();
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(candidateEntity, result.get(0));
//        verify(candidateRepository, times(1)).findAll();
//    }
//
//    @Test
//    void getCandidatesFiltered_WithFilter_ReturnsPagedCandidates() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<CandidateEntity> candidatePage = new PageImpl<>(Collections.singletonList(candidateEntity), pageable, 1);
//
//        // Mock the specification to support chaining
//        Specification<CandidateEntity> specification = mock(Specification.class);
//        when(specification.and(any(Specification.class))).thenReturn(specification); // Ensure chaining returns non-null
//        when(candidatesSpecification.propertyLikeIgnoreCase(eq("firstName"), eq("John"))).thenReturn(specification);
//        when(candidatesSpecification.propertyInSkills(eq(Collections.singletonList(skillDto)))).thenReturn(specification);
//        when(candidatesSpecification.empty()).thenReturn(specification);
//        when(candidateRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(candidatePage);
//
//        // When
//        Page<CandidateEntity> result = candidateService.getCandidatesFiltered(candidateFilter, pageable);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals(candidateEntity, result.getContent().get(0));
//        verify(candidateRepository, times(1)).findAll(any(Specification.class), eq(pageable));
//        verify(candidatesSpecification, times(1)).propertyLikeIgnoreCase(eq("firstName"), eq("John"));
//        verify(candidatesSpecification, times(1)).propertyInSkills(eq(Collections.singletonList(skillDto)));
//        verify(candidatesSpecification, atLeastOnce()).empty();
//    }
//
//    @Test
//    void getCandidateById_ExistingId_ReturnsCandidate() {
//        // Given
//        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidateEntity));
//
//        // When
//        CandidateEntity result = candidateService.getCandidateById(candidateId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(candidateEntity, result);
//        verify(candidateRepository, times(1)).findById(candidateId);
//    }
//
//    @Test
//    void getCandidateById_NonExistingId_ThrowsEntityNotFoundException() {
//        // Given
//        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());
//
//        // When & Then
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                () -> candidateService.getCandidateById(candidateId));
//        assertEquals("Candidate not found with ID: " + candidateId, exception.getMessage());
//        verify(candidateRepository, times(1)).findById(candidateId);
//    }
//
//    @Test
//    void getCandidateByEmail_ExistingEmail_ReturnsCandidate() {
//        // Given
//        when(candidateRepository.findByEmail(candidateEmail)).thenReturn(Optional.of(candidateEntity));
//
//        // When
//        CandidateEntity result = candidateService.getCandidateByEmail(candidateEmail);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(candidateEntity, result);
//        verify(candidateRepository, times(1)).findByEmail(candidateEmail);
//    }
//
//    @Test
//    void getCandidateByEmail_NonExistingEmail_ThrowsEntityNotFoundException() {
//        // Given
//        when(candidateRepository.findByEmail(candidateEmail)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(NoSuchElementException.class, () -> candidateService.getCandidateByEmail(candidateEmail));
//        verify(candidateRepository, times(1)).findByEmail(candidateEmail);
//    }
//
//    @Test
//    void addCandidate_ValidDto_SavesAndReturnsCandidate() {
//        // Given
//        when(modelMapper.map(candidateDto, CandidateEntity.class)).thenReturn(candidateEntity);
//        when(candidateRepository.save(candidateEntity)).thenReturn(candidateEntity);
//
//        // When
//        CandidateEntity result = candidateService.addCandidate(candidateDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(candidateEntity, result);
//        verify(modelMapper, times(1)).map(candidateDto, CandidateEntity.class);
//        verify(candidateRepository, times(1)).save(candidateEntity);
//    }
//
//    @Test
//    void updateCandidate_WithExistingSkill_UpdatesProficiencyLevel() {
//        // Given
//        candidateDto.setSkills(Collections.singletonList(skillDto));
//        when(candidateRepository.findByEmail(candidateEmail)).thenReturn(Optional.of(candidateEntity));
//        when(skillService.findByName(skillName)).thenReturn(skillEntity);
//        when(candidateSkillRepository.findByCandidateIdAndSkillId(candidateId, skillId))
//                .thenReturn(Optional.of(candidateSkill)) // First call: skill exists
//                .thenReturn(Optional.of(candidateSkill)); // Second call: retrieve updated skill
//        when(candidateSkillRepository.updateProficiencyLevel(candidateId, skillId, proficiencyLevel)).thenReturn(1);
//        when(candidateRepository.save(candidateEntity)).thenReturn(candidateEntity);
//
//        // When
//        CandidateEntity result = candidateService.updateCandidate(candidateDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getSkills().size());
//        assertEquals(candidateSkill, result.getSkills().get(0));
//        assertEquals(proficiencyLevel, result.getSkills().get(0).getProficiencyLevel());
//        verify(candidateRepository, times(1)).findByEmail(candidateEmail);
//        verify(skillService, times(1)).findByName(skillName);
//        verify(candidateSkillRepository, times(2)).findByCandidateIdAndSkillId(candidateId, skillId); // Expect 2 calls
//        verify(candidateSkillRepository, times(1)).updateProficiencyLevel(candidateId, skillId, proficiencyLevel);
//        verify(candidateSkillRepository, never()).save(any(CandidateSkill.class));
//        verify(candidateRepository, times(1)).save(candidateEntity);
//    }
//
//    @Test
//    void updateCandidate_WithNewSkill_AddsNewSkill() {
//        // Given
//        candidateDto.setSkills(Collections.singletonList(skillDto));
//        when(candidateRepository.findByEmail(candidateEmail)).thenReturn(Optional.of(candidateEntity));
//        when(skillService.findByName(skillName)).thenReturn(skillEntity);
//        when(candidateSkillRepository.findByCandidateIdAndSkillId(candidateId, skillId))
//                .thenReturn(Optional.empty())
//                .thenReturn(Optional.of(candidateSkill));
//        when(candidateSkillRepository.save(any(CandidateSkill.class))).thenReturn(candidateSkill);
//        when(candidateRepository.save(candidateEntity)).thenReturn(candidateEntity);
//
//        // When
//        CandidateEntity result = candidateService.updateCandidate(candidateDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getSkills().size());
//        assertEquals(candidateSkill, result.getSkills().get(0));
//        assertEquals(proficiencyLevel, result.getSkills().get(0).getProficiencyLevel());
//        verify(candidateRepository, times(1)).findByEmail(candidateEmail);
//        verify(skillService, times(1)).findByName(skillName);
//        verify(candidateSkillRepository, times(2)).findByCandidateIdAndSkillId(candidateId, skillId);
//        verify(candidateSkillRepository, times(1)).save(any(CandidateSkill.class));
//        verify(candidateSkillRepository, never()).updateProficiencyLevel(anyLong(), anyLong(), anyString());
//        verify(candidateRepository, times(1)).save(candidateEntity);
//    }
//
//    @Test
//    void updateCandidate_NonExistingEmail_ThrowsEntityNotFoundException() {
//        // Given
//        when(candidateRepository.findByEmail(candidateEmail)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(NoSuchElementException.class, () -> candidateService.updateCandidate(candidateDto));
//        verify(candidateRepository, times(1)).findByEmail(candidateEmail);
//        verify(skillService, never()).findByName(anyString());
//        verify(candidateSkillRepository, never()).findByCandidateIdAndSkillId(anyLong(), anyLong());
//        verify(candidateRepository, never()).save(any());
//    }
//
//    @Test
//    void updateCandidate_WithEmptySkills_ClearsSkills() {
//        // Given
//        candidateEntity.getSkills().add(candidateSkill);
//        candidateDto.setSkills(Collections.emptyList());
//        when(candidateRepository.findByEmail(candidateEmail)).thenReturn(Optional.of(candidateEntity));
//        when(candidateRepository.save(candidateEntity)).thenReturn(candidateEntity);
//
//        // When
//        CandidateEntity result = candidateService.updateCandidate(candidateDto);
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.getSkills().isEmpty());
//        verify(candidateRepository, times(1)).findByEmail(candidateEmail);
//        verify(skillService, never()).findByName(anyString());
//        verify(candidateSkillRepository, never()).findByCandidateIdAndSkillId(anyLong(), anyLong());
//        verify(candidateSkillRepository, never()).save(any(CandidateSkill.class));
//        verify(candidateRepository, times(1)).save(candidateEntity);
//    }
//
//    @Test
//    void updateCandidate_ExistingCandidate_UpdatesFields() {
//        // Given
//        CandidateDto updatedDto = new CandidateDto();
//        updatedDto.setPhone("9876543210");
//        updatedDto.setProfilePictureUrl("https://example.com/new-profile.jpg");
//        updatedDto.setHeadline("Senior Developer");
//        updatedDto.setSummary("Highly experienced developer");
//        updatedDto.setExperience("10 years in Java");
//        updatedDto.setYearsOfExperience(10);
//        updatedDto.setEducation("MSc Computer Science");
//        updatedDto.setCertifications("Azure Certified");
//        updatedDto.setWorkExperiences("Worked at ABC Corp");
//        updatedDto.setProjects("Built a cloud platform");
//        updatedDto.setMediaUrl("https://example.com/new-portfolio");
//        updatedDto.setSalaryExpectation("120000 USD");
//        updatedDto.setWorkStyle(CandidateDto.WorkStyleEnum.HYBRID);
//        updatedDto.setAppliedDate(LocalDate.of(2025, 6, 2));
//        updatedDto.setLocation("San Francisco");
//
//        when(candidateRepository.save(any(CandidateEntity.class))).thenReturn(candidateEntity);
//
//        // When
//        CandidateEntity result = candidateService.updateCandidate(candidateEntity, updatedDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(updatedDto.getPhone(), result.getPhone());
//        assertEquals(updatedDto.getProfilePictureUrl(), result.getProfilePictureUrl());
//        assertEquals(updatedDto.getHeadline(), result.getHeadline());
//        assertEquals(updatedDto.getSummary(), result.getSummary());
//        assertEquals(updatedDto.getExperience(), result.getExperience());
//        assertEquals(updatedDto.getYearsOfExperience(), result.getYearsOfExperience());
//        assertEquals(updatedDto.getEducation(), result.getEducation());
//        assertEquals(updatedDto.getCertifications(), result.getCertifications());
//        assertEquals(updatedDto.getWorkExperiences(), result.getWorkExperiences());
//        assertEquals(updatedDto.getProjects(), result.getProjects());
//        assertEquals(updatedDto.getMediaUrl(), result.getMediaUrl());
//        assertEquals(updatedDto.getSalaryExpectation(), result.getSalaryExpectation());
//        assertEquals(updatedDto.getWorkStyle().toString(), result.getWorkStyle());
//        assertEquals(updatedDto.getAppliedDate(), result.getAppliedDate());
//        assertEquals(updatedDto.getLocation(), result.getLocation());
//        verify(candidateRepository, times(1)).save(candidateEntity);
//    }
//
//    @Test
//    void deleteCandidate_ExistingId_DeletesCandidate() {
//        // Given
//        doNothing().when(candidateRepository).deleteById(candidateId);
//
//        // When
//        candidateService.deleteCandidate(candidateId);
//
//        // Then
//        verify(candidateRepository, times(1)).deleteById(candidateId);
//    }
//
//    @Test
//    void getSpecification_WithFilter_ReturnsCombinedSpecification() throws Exception {
//        // Given
//        Specification<CandidateEntity> specification = mock(Specification.class);
//        when(specification.and(any(Specification.class))).thenReturn(specification); // Support chaining
//        when(candidatesSpecification.propertyLikeIgnoreCase(anyString(), anyString())).thenReturn(specification);
//        when(candidatesSpecification.propertyInSkills(anyList())).thenReturn(specification);
//        when(candidatesSpecification.empty()).thenReturn(specification);
//
//        // When
//        Method getSpecificationMethod = candidateService.getClass()
//                .getDeclaredMethod("getSpecification", CandidateFilter.class);
//        getSpecificationMethod.setAccessible(true); // Make private method accessible
//        Specification<CandidateEntity> result = (Specification<CandidateEntity>) getSpecificationMethod
//                .invoke(candidateService, candidateFilter);
//
//        // Then
//        assertNotNull(result);
//        verify(candidatesSpecification, atLeastOnce()).propertyLikeIgnoreCase(eq("firstName"), eq("John"));
//        verify(candidatesSpecification, atLeastOnce()).propertyInSkills(eq(Collections.singletonList(skillDto)));
//        verify(candidatesSpecification, atLeastOnce()).empty();
//    }
//}