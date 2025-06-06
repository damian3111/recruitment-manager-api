//package com.damian3111.recruitment_manager_api.services;
//
//import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
//import com.damian3111.recruitment_manager_api.persistence.entities.JobSkill;
//import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
//import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
//import com.damian3111.recruitment_manager_api.persistence.repositories.JobRepository;
//import com.damian3111.recruitment_manager_api.persistence.repositories.JobSkillRepository;
//import com.damian3111.recruitment_manager_api.persistence.specification.JobSpecification;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//import org.openapitools.model.JobDto;
//import org.openapitools.model.JobDtoSkillsInner;
//import org.openapitools.model.JobFilter;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.lang.reflect.Method;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class JobServiceTest {
//
//    @Mock
//    private JobRepository jobRepository;
//
//    @Mock
//    private JobSkillRepository jobSkillRepository;
//
//    @Mock
//    private SkillService skillService;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @Mock
//    private JobSpecification jobSpecification;
//
//    @InjectMocks
//    private JobService jobService;
//
//    private JobEntity jobEntity;
//    private JobDto jobDto;
//    private SkillEntity skillEntity;
//    private JobSkill jobSkill;
//    private JobDtoSkillsInner skillDto;
//    private JobFilter jobFilter;
//    private UserEntity userEntity;
//    private final Long jobId = 1L;
//    private final Long skillId = 1L;
//    private final String skillName = "Java";
//    private final String proficiencyLevel = "Expert";
//    private final String userEmail = "user@example.com";
//    private final Long userId = 1L;
//
//    @BeforeEach
//    void setUp() {
//        // Initialize JobEntity
//        jobEntity = new JobEntity();
//        jobEntity.setId(jobId);
//        jobEntity.setTitle("Software Engineer");
//        jobEntity.setDescription("Develop software solutions");
//        jobEntity.setRequirements("5 years of experience");
//        jobEntity.setResponsibilities("Write clean code");
//        jobEntity.setEmploymentType("Full-time");
//        jobEntity.setLocation("New York");
//        jobEntity.setSalaryMin(new BigDecimal("80000"));
//        jobEntity.setSalaryMax(new BigDecimal("120000"));
//        jobEntity.setCurrency("USD");
//        jobEntity.setExperienceLevel("Senior");
//        jobEntity.setIndustry("Tech");
//        jobEntity.setCompanyName("Tech Corp");
//        jobEntity.setBenefits("Health insurance");
//        jobEntity.setEmploymentMode("Remote");
//        jobEntity.setPostedDate(LocalDate.of(2025, 6, 1));
//        jobEntity.setApplicationDeadline(LocalDate.of(2025, 6, 30));
//        jobEntity.setSkills(new ArrayList<>());
//
//        // Initialize UserEntity
//        userEntity = new UserEntity();
//        userEntity.setId(userId);
//        userEntity.setEmail(userEmail);
//
//        jobEntity.setUser(userEntity);
//
//        // Initialize JobDto
//        jobDto = new JobDto();
//        jobDto.setTitle("Software Engineer");
//        jobDto.setDescription("Develop software solutions");
//        jobDto.setRequirements("5 years of experience");
//        jobDto.setResponsibilities("Write clean code");
//        jobDto.setEmploymentType(JobDto.EmploymentTypeEnum.FULL_TIME);
//        jobDto.setLocation("New York");
//        jobDto.setSalaryMin(80000d);
//        jobDto.setSalaryMax(120000d);
//        jobDto.setCurrency("USD");
//        jobDto.setExperienceLevel(JobDto.ExperienceLevelEnum.SENIOR);
//        jobDto.setIndustry("Tech");
//        jobDto.setCompany("Tech Corp");
//        jobDto.setBenefits("Health insurance");
//        jobDto.setEmploymentMode(JobDto.EmploymentModeEnum.REMOTE);
//        jobDto.setPostedDate(LocalDate.of(2025, 6, 1));
//        jobDto.setApplicationDeadline(LocalDate.of(2025, 6, 30));
//        jobDto.setSkills(new ArrayList<>());
//
//        // Initialize SkillEntity
//        skillEntity = new SkillEntity();
//        skillEntity.setId(skillId);
//        skillEntity.setName(skillName);
//        skillEntity.setCreatedAt(LocalDate.of(2025, 6, 1));
//
//        // Initialize JobSkill
//        jobSkill = JobSkill.builder()
//                .job(jobEntity)
//                .skill(skillEntity)
//                .proficiencyLevel(proficiencyLevel)
//                .build();
//
//        // Initialize JobDtoSkillsInner
//        skillDto = new JobDtoSkillsInner();
//        skillDto.setName(skillName);
//        skillDto.setProficiencyLevel(JobDtoSkillsInner.ProficiencyLevelEnum.EXPERT);
//
//        // Initialize JobFilter
//        jobFilter = new JobFilter();
//        jobFilter.setTitle("Software Engineer");
//        jobFilter.setSkills(Collections.singletonList(skillDto));
//    }
//
//    @Test
//    void getAllJobs_ReturnsAllJobs() {
//        // Given
//        List<JobEntity> jobs = Collections.singletonList(jobEntity);
//        when(jobRepository.findAll()).thenReturn(jobs);
//
//        // When
//        List<JobEntity> result = jobService.getAllJobs();
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(jobEntity, result.get(0));
//        verify(jobRepository, times(1)).findAll();
//    }
//
//    @Test
//    void getJobsByUserId_ExistingUserId_ReturnsJobs() {
//        // Given
//        List<JobEntity> jobs = Collections.singletonList(jobEntity);
//        when(jobRepository.findByUserId(userId)).thenReturn(Optional.of(jobs));
//
//        // When
//        List<JobEntity> result = jobService.getJobsByUserId(userId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(jobEntity, result.get(0));
//        verify(jobRepository, times(1)).findByUserId(userId);
//    }
//
//    @Test
//    void getJobsByUserId_NonExistingUserId_ThrowsEntityNotFoundException() {
//        // Given
//        when(jobRepository.findByUserId(userId)).thenReturn(Optional.empty());
//
//        // When & Then
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                () -> jobService.getJobsByUserId(userId));
//        assertEquals("No jobs found for user ID: " + userId, exception.getMessage());
//        verify(jobRepository, times(1)).findByUserId(userId);
//    }
//
//    @Test
//    void getJobById_ExistingId_ReturnsJob() {
//        // Given
//        when(jobRepository.findById(jobId)).thenReturn(Optional.of(jobEntity));
//
//        // When
//        JobEntity result = jobService.getJobById(jobId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(jobEntity, result);
//        verify(jobRepository, times(1)).findById(jobId);
//    }
//
//    @Test
//    void getJobById_NonExistingId_ThrowsEntityNotFoundException() {
//        // Given
//        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
//
//        // When & Then
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                () -> jobService.getJobById(jobId));
//        assertEquals("Job not found with ID: " + jobId, exception.getMessage());
//        verify(jobRepository, times(1)).findById(jobId);
//    }
//
//    @Test
//    void getJobsFiltered_WithFilter_ReturnsPagedJobs() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<JobEntity> jobPage = new PageImpl<>(Collections.singletonList(jobEntity), pageable, 1);
//        Specification<JobEntity> specification = mock(Specification.class);
//        when(specification.and(any(Specification.class))).thenReturn(specification);
//        when(jobSpecification.propertyLikeIgnoreCase(eq("title"), eq("Software Engineer"))).thenReturn(specification);
//        List<JobDtoSkillsInner> skillNames = Collections.singletonList(skillDto);
//        when(jobSpecification.propertyInSkills(eq(skillNames))).thenReturn(specification);
//        when(jobSpecification.empty()).thenReturn(specification);
//        when(jobRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(jobPage);
//
//        // When
//        Page<JobEntity> result = jobService.getCandidatesFiltered(jobFilter, pageable);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals(jobEntity, result.getContent().get(0));
//        verify(jobRepository, times(1)).findAll(any(Specification.class), eq(pageable));
//        verify(jobSpecification, times(1)).propertyLikeIgnoreCase(eq("title"), eq("Software Engineer"));
//        verify(jobSpecification, times(1)).propertyInSkills(eq(skillNames));
//        verify(jobSpecification, atLeastOnce()).empty();
//    }
//
//    @Test
//    void createJob_ValidDto_CreatesAndReturnsJob() {
//        // Given
//        jobDto.setSkills(Collections.singletonList(skillDto));
//        when(modelMapper.map(jobDto, JobEntity.class)).thenReturn(jobEntity);
//        when(userService.getUserByEmail(userEmail)).thenReturn(userEntity);
//        when(jobRepository.save(any(JobEntity.class))).thenReturn(jobEntity);
//        when(skillService.findByName(skillName)).thenReturn(skillEntity);
//        when(jobSkillRepository.save(any(JobSkill.class))).thenReturn(jobSkill);
//
//        // Set up SecurityContext
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn(userEmail);
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        // When
//        JobEntity result = jobService.createJob(jobDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(jobEntity, result);
//        assertEquals(1, result.getSkills().size());
//        assertEquals(jobSkill, result.getSkills().get(0));
//        verify(modelMapper, times(1)).map(jobDto, JobEntity.class);
//        verify(userService, times(1)).getUserByEmail(userEmail);
//        verify(jobRepository, times(1)).save(any(JobEntity.class));
//        verify(skillService, times(1)).findByName(skillName);
//        verify(jobSkillRepository, times(1)).save(any(JobSkill.class));
//    }
//
////    @Test
////    void updateJob_ExistingJob_UpdatesFieldsAndSkills() {
////        // Given
////        jobDto.setSkills(Collections.singletonList(skillDto));
////        when(jobRepository.findById(jobId)).thenReturn(Optional.of(jobEntity));
////        when(skillService.findOrCreateSkill(skillName)).thenReturn(skillEntity);
////        when(jobSkillRepository.save(any(JobSkill.class))).thenReturn(jobSkill);
////        when(jobRepository.save(jobEntity)).thenReturn(jobEntity);
////        doNothing().when(modelMapper).map(jobDto, jobEntity);
////        when(modelMapper.map(jobEntity, JobDto.class)).thenReturn(jobDto);
////
////        // When
////        JobDto result = jobService.updateJob(jobId, jobDto);
////
////        // Then
////        assertNotNull(result);
////        assertEquals(jobDto.getTitle(), result.getTitle());
////        assertEquals(1, result.getSkills().size());
////        assertEquals(skillName, result.getSkills().get(0).getName());
////        verify(jobRepository, times(1)).findById(jobId);
////        verify(modelMapper, times(1)).map(jobDto, jobEntity);
////        verify(modelMapper, times(1)).map(jobEntity, JobDto.class);
////        verify(skillService, times(1)).findOrCreateSkill(skillName);
////        verify(jobRepository, times(1)).save(jobEntity);
////        verify(jobSkillRepository, never()).save(any(JobSkill.class)); // updateJobSkills is empty
////    }
//
//    @Test
//    void updateJob_NonExistingJob_ThrowsEntityNotFoundException() {
//        // Given
//        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
//
//        // When & Then
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                () -> jobService.updateJob(jobId, jobDto));
//        assertEquals("Job not found with ID: " + jobId, exception.getMessage());
//        verify(jobRepository, times(1)).findById(jobId);
//        verify(jobRepository, never()).save(any());
//        verify(modelMapper, never()).map(any(), any());
//    }
//
//    @Test
//    void deleteJob_ExistingId_DeletesJob() {
//        // Given
//        when(jobRepository.existsById(jobId)).thenReturn(true);
//        doNothing().when(jobRepository).deleteById(jobId);
//
//        // When
//        jobService.deleteJob(jobId);
//
//        // Then
//        verify(jobRepository, times(1)).existsById(jobId);
//        verify(jobRepository, times(1)).deleteById(jobId);
//    }
//
//    @Test
//    void deleteJob_NonExistingId_ThrowsEntityNotFoundException() {
//        // Given
//        when(jobRepository.existsById(jobId)).thenReturn(false);
//
//        // When & Then
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                () -> jobService.deleteJob(jobId));
//        assertEquals("Job not found with ID: " + jobId, exception.getMessage());
//        verify(jobRepository, times(1)).existsById(jobId);
//        verify(jobRepository, never()).deleteById(jobId);
//    }
//
//    @Test
//    void getSpecification_WithFilter_ReturnsCombinedSpecification() throws Exception {
//        // Given
//        Specification<JobEntity> specification = mock(Specification.class);
//        when(specification.and(any(Specification.class))).thenReturn(specification);
//        when(jobSpecification.propertyLikeIgnoreCase(eq("title"), eq("Software Engineer"))).thenReturn(specification);
//        List<JobDtoSkillsInner> skillNames = Collections.singletonList(skillDto);
//        when(jobSpecification.propertyInSkills(eq(skillNames))).thenReturn(specification);
//        when(jobSpecification.empty()).thenReturn(specification);
//
//        // When
//        Method getSpecificationMethod = jobService.getClass()
//                .getDeclaredMethod("getSpecification", JobFilter.class);
//        getSpecificationMethod.setAccessible(true);
//        Specification<JobEntity> result = (Specification<JobEntity>) getSpecificationMethod
//                .invoke(jobService, jobFilter);
//
//        // Then
//        assertNotNull(result);
//        verify(jobSpecification, atLeastOnce()).propertyLikeIgnoreCase(eq("title"), eq("Software Engineer"));
//        verify(jobSpecification, atLeastOnce()).propertyInSkills(eq(skillNames));
//        verify(jobSpecification, atLeastOnce()).empty();
//    }
//}