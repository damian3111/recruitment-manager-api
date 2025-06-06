//package com.damian3111.recruitment_manager_api.controllers;
//
//import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
//import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkill;
//import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
//import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateRepository;
//import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateSkillRepository;
//import com.damian3111.recruitment_manager_api.persistence.repositories.SkillRepository;
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.ValidatableResponse;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.openapitools.model.CandidateDto;
//import org.openapitools.model.CandidateDtoSkillsInner;
//import org.openapitools.model.CandidateFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//class CandidateControllerIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private CandidateRepository candidateRepository;
//
//    @Autowired
//    private CandidateSkillRepository candidateSkillRepository;
//
//    @Autowired
//    private SkillRepository skillRepository;
//
//    private static final String BASE_URL = "/api/candidates";
//    private static final String VALID_EMAIL = "john.doe@example.com";
//    private static final String SKILL_NAME = "Java";
//    private static final String PROFICIENCY_LEVEL = "Expert";
//
//    @BeforeEach
//    void setUp() {
//        RestAssured.port = port;
//        // Clear database
//        candidateSkillRepository.deleteAllInBatch();
//        candidateRepository.deleteAllInBatch();
//        skillRepository.deleteAllInBatch();
//    }
//
//    @AfterEach
//    void tearDown() {
//        candidateSkillRepository.deleteAllInBatch();
//        candidateRepository.deleteAllInBatch();
//        skillRepository.deleteAllInBatch();
//    }
//
//    @Test
//    void updateCandidate_ExistingCandidate_UpdatesFieldsAndSkills() {
//        CandidateEntity savedCandidate = saveCandidateEntity(VALID_EMAIL);
//
//        CandidateDto updatedCandidateDto = createCandidateDto();
//        updatedCandidateDto.setEmail(VALID_EMAIL);
//        updatedCandidateDto.setHeadline("Updated Senior Developer");
//        updatedCandidateDto.getSkills().get(0).setProficiencyLevel(CandidateDtoSkillsInner.ProficiencyLevelEnum.EXPERT);
//
//        ValidatableResponse response = given()
//                .contentType(ContentType.JSON)
//                .body(updatedCandidateDto)
//                .log().all()
//                .when()
//                .put("candidates/update/{id}", savedCandidate.getId())
//                .then()
//                .log().all() // Log response details
//                .statusCode(HttpStatus.OK.value())
//                .body("email", equalTo(VALID_EMAIL))
//                .body("headline", equalTo("Updated Senior Developer"));
////                .body("skills[0].proficiencyLevel", equalTo("EXPERT"));
//
//        CandidateEntity updatedEntity = candidateRepository.findByIdWithSkills(savedCandidate.getId()).orElseThrow();
//        assert updatedEntity.getHeadline().equals("Updated Senior Developer");
//        assert updatedEntity.getSkills().get(0).getProficiencyLevel().equals("Expert");
//    }
//
//    private CandidateDto createCandidateDto() {
//        CandidateDto candidateDto = new CandidateDto();
//        candidateDto.setFirstName("John");
//        candidateDto.setLastName("Doe");
//        candidateDto.setEmail(VALID_EMAIL);
//        candidateDto.setPhone("+1234567890");
//        candidateDto.setHeadline("Senior Developer");
//        candidateDto.setSummary("Experienced developer with 10 years in Java.");
//        candidateDto.setExperience("Java, Spring, Microservices");
//        candidateDto.setYearsOfExperience(10);
//        candidateDto.setEducation("BSc Computer Science");
//        candidateDto.setCertifications("AWS Certified Developer");
//        candidateDto.setWorkExperiences("Lead Developer at Tech Corp");
//        candidateDto.setProjects("E-commerce platform");
//        candidateDto.setMediaUrl("http://portfolio.com/john");
//        candidateDto.setSalaryExpectation("100000");
//        candidateDto.setWorkStyle(CandidateDto.WorkStyleEnum.REMOTE);
//        candidateDto.setAppliedDate(LocalDate.of(2025, 6, 1));
//        candidateDto.setLocation("New York");
//
//        CandidateDtoSkillsInner skill = new CandidateDtoSkillsInner()
//                .name(SKILL_NAME)
//                .proficiencyLevel(CandidateDtoSkillsInner.ProficiencyLevelEnum.EXPERT);
//        candidateDto.setSkills(Collections.singletonList(skill));
//
//        return candidateDto;
//    }
//    @Transactional
//    protected CandidateEntity saveCandidateEntity(String email) {
//        SkillEntity skillEntity = SkillEntity.builder()
//                .name(SKILL_NAME)
//                .createdAt(LocalDate.of(2025, 6, 1))
//                .build();
//
//        CandidateEntity candidateEntity = CandidateEntity.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .email(email)
//                .phone("+1234567890")
//                .headline("Senior Developer")
//                .summary("Experienced developer with 10 years in Java.")
//                .experience("Java, Spring, Microservices")
//                .yearsOfExperience(10)
//                .education("BSc Computer Science")
//                .certifications("AWS Certified Developer")
//                .workExperiences("Lead Developer at Tech Corp")
//                .projects("E-commerce platform")
//                .mediaUrl("http://portfolio.com/john")
//                .salaryExpectation("100000")
//                .workStyle("Remote")
//                .appliedDate(LocalDate.of(2025, 6, 1))
//                .location("New York")
//                .skills(new ArrayList<>())
//                .build();
//
//        CandidateSkill candidateSkill = CandidateSkill.builder()
//                .candidate(candidateEntity)
//                .skill(skillEntity)
//                .proficiencyLevel(PROFICIENCY_LEVEL)
//                .build();
//        candidateEntity.getSkills().add(candidateSkill);
//
//        return candidateRepository.save(candidateEntity);
//    }
//    @Transactional
//    protected CandidateEntity saveCandidateEntity2(String email) {
//        SkillEntity skillEntity = SkillEntity.builder()
//                .name("Java Script")
//                .createdAt(LocalDate.of(2025, 6, 1))
//                .build();
//
//        CandidateEntity candidateEntity = CandidateEntity.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .email(email)
//                .phone("+1234567890")
//                .headline("Senior Developer")
//                .summary("Experienced developer with 10 years in Java.")
//                .experience("Java, Spring, Microservices")
//                .yearsOfExperience(10)
//                .education("BSc Computer Science")
//                .certifications("AWS Certified Developer")
//                .workExperiences("Lead Developer at Tech Corp")
//                .projects("E-commerce platform")
//                .mediaUrl("http://portfolio.com/john")
//                .salaryExpectation("100000")
//                .workStyle("Remote")
//                .appliedDate(LocalDate.of(2025, 6, 1))
//                .location("New York")
//                .skills(new ArrayList<>())
//                .build();
//
//        CandidateSkill candidateSkill = CandidateSkill.builder()
//                .candidate(candidateEntity)
//                .skill(skillEntity)
//                .proficiencyLevel(PROFICIENCY_LEVEL)
//                .build();
//        candidateEntity.getSkills().add(candidateSkill);
//
//        return candidateRepository.save(candidateEntity);
//    }
//    @Test
//    void addCandidate_ValidInput_ReturnsCreatedCandidate() {
//        CandidateDto candidateDto = createCandidateDto();
//
//        SkillEntity skillEntity = SkillEntity.builder()
//                .name(SKILL_NAME)
//                .createdAt(LocalDate.of(2025, 6, 1))
//                .build();
//        skillRepository.save(skillEntity);
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(candidateDto)
//                .when()
//                .post("/candidates")
//                .then()
//                .statusCode(HttpStatus.CREATED.value())
//                .body("id", notNullValue())
//                .body("email", equalTo(VALID_EMAIL))
//                .body("skills.size()", equalTo(1));
////                .body("skills[0].name", equalTo(SKILL_NAME))
////                .body("skills[0].proficiencyLevel", equalTo(PROFICIENCY_LEVEL));
//
//        List<CandidateEntity> candidates = candidateRepository.findAllWithSkillsAndSkill();
//        assert candidates.size() == 1;
//        CandidateEntity savedCandidate = candidates.get(0);
//        assert savedCandidate.getEmail().equals(VALID_EMAIL);
//        assert savedCandidate.getSkills().size() == 1;
//        assert savedCandidate.getSkills().get(0).getSkill().getName().equals(SKILL_NAME);
//        assert savedCandidate.getSkills().get(0).getProficiencyLevel().equals(PROFICIENCY_LEVEL);
//
//        Optional<CandidateSkill> candidateSkill = candidateSkillRepository.findByCandidateIdAndSkillId(
//                savedCandidate.getId(), skillEntity.getId());
//        assert candidateSkill.isPresent();
//        assert candidateSkill.get().getProficiencyLevel().equals(PROFICIENCY_LEVEL);
//    }
//
////    @Test
////    void addCandidate_InvalidEmail_ReturnsBadRequest() {
////        CandidateDto candidateDto = createCandidateDto();
////        candidateDto.setEmail("invalid-email");
////
////        SkillEntity skillEntity = SkillEntity.builder()
////                .name(SKILL_NAME)
////                .createdAt(LocalDate.of(2025, 6, 1))
////                .build();
////        skillRepository.save(skillEntity);
////
////        given()
////                .contentType(ContentType.JSON)
////                .body(candidateDto)
////                .when()
////                .post("/candidates")
////                .then()
////                .statusCode(HttpStatus.BAD_REQUEST.value())
////                .body("message", containsString("Invalid email format"));
////    }
//
////    @Test
////    void getAllCandidates_MultipleCandidates_ReturnsList() {
////        // Setup two candidates
////        saveCandidateEntity("alice.smith@example.com");
////        saveCandidateEntity("bob.jones@example.com");
////
////        given()
////                .contentType(ContentType.JSON)
////                .when()
////                .get(BASE_URL)
////                .then()
////                .statusCode(HttpStatus.OK.value())
////                .body("size()", equalTo(2))
////                .body("[0].email", equalTo("alice.smith@example.com"))
////                .body("[1].email", equalTo("bob.jones@example.com"));
////    }
//
//    @Test
//    void getCandidateById_ExistingId_ReturnsCandidate() {
//        CandidateEntity savedCandidate = saveCandidateEntity(VALID_EMAIL);
//
//        given()
//                .contentType(ContentType.JSON)
//                .when()
//                .get("/candidates/{id}", savedCandidate.getId())
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body("id", equalTo(savedCandidate.getId().intValue()))
//                .body("email", equalTo(VALID_EMAIL))
//                .body("skills[0].name", equalTo(SKILL_NAME));
//    }
//
//    @Test
//    void getCandidateById_NonExistentId_ReturnsNotFound() {
//        given()
////                .contentType(ContentType.JSON)
//                .when()
//                .get( "candidates/999")
//                .then()
//                .statusCode(HttpStatus.NOT_FOUND.value());
//    }
//
//    @Test
//    void getCandidateByEmail_ExistingEmail_ReturnsCandidate() {
//        saveCandidateEntity(VALID_EMAIL);
//
//        given()
//                .contentType(ContentType.JSON)
//                .queryParam("email", VALID_EMAIL) // Pass email as query parameter
//                .log().all() // Log request
//                .when()
//                .get("/candidates/by-email")
//                .then()
//                .log().all() // Log response
//                .statusCode(HttpStatus.OK.value())
//                .body("email", equalTo(VALID_EMAIL))
//                .body("skills[0].name", equalTo(SKILL_NAME))
//                .body("skills[0].proficiencyLevel", equalTo(PROFICIENCY_LEVEL));
//    }
//
//    @Test
//    void getCandidateByEmail_NonExistentEmail_ReturnsNotFound() {
//        given()
//                .contentType(ContentType.JSON)
//                .when()
//                .get(BASE_URL + "/email/nonexistent@example.com")
//                .then()
//                .statusCode(HttpStatus.NOT_FOUND.value());
//    }
//
//    @Test
//    void filterCandidates_BySkill_ReturnsFilteredPage() {
//        saveCandidateEntity(VALID_EMAIL);
//        saveCandidateEntity2("other@example.com");
//
//        CandidateFilter filter = new CandidateFilter();
//        filter.setSkills(Collections.singletonList(new CandidateDtoSkillsInner()
//                .name(SKILL_NAME)
//                .proficiencyLevel(CandidateDtoSkillsInner.ProficiencyLevelEnum.EXPERT)));
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(filter)
//                .queryParam("page", 0)
//                .queryParam("size", 10)
//                .when()
//                .post("/candidates/filter")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body("content.size()", equalTo(1))
//                .body("content[0].email", equalTo(VALID_EMAIL))
//                .body("totalElements", equalTo(1));
//    }
//
//    @Test
//    void updateCandidate_NonExistentEmail_ReturnsNotFound() {
//        CandidateDto candidateDto = createCandidateDto();
//        candidateDto.setEmail("nonexistent@example.com");
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(candidateDto)
//                .when()
//                .put(BASE_URL + "/1")
//                .then()
//                .statusCode(HttpStatus.NOT_FOUND.value());
//    }
//}