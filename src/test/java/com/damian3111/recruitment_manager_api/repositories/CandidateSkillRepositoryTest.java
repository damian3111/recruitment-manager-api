package com.damian3111.recruitment_manager_api.repositories;


import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkill;
import com.damian3111.recruitment_manager_api.persistence.entities.CandidateSkillId;
import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.CandidateSkillRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CandidateSkillRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private CandidateSkillRepository candidateSkillRepository;

    @Autowired
    private JpaRepository<CandidateEntity, Long> candidateRepository;

    @Autowired
    private JpaRepository<SkillEntity, Long> skillRepository;

    private CandidateEntity candidate;
    private SkillEntity skill;
    private CandidateSkill candidateSkill;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        candidateSkillRepository.deleteAll();
        candidateRepository.deleteAll();
        skillRepository.deleteAll();

        candidate = CandidateEntity.builder()
                .email("test@candidate.com")
                .firstName("Test")
                .lastName("Candidate")
                .build();
        candidateRepository.save(candidate);

        skill = SkillEntity.builder()
                .name("Java")
                .build();
        skillRepository.save(skill);

        candidateSkill = CandidateSkill.builder()
                .candidate(candidate)
                .skill(skill)
                .proficiencyLevel("Expert")
                .build();
        candidateSkillRepository.save(candidateSkill);
    }

    @Test
    void findByCandidateIdAndSkillId_shouldReturnCandidateSkillWhenFound() {
        // Given
        Long candidateId = candidate.getId();
        Long skillId = skill.getId();

        // When
        Optional<CandidateSkill> result = candidateSkillRepository.findByCandidateIdAndSkillId(candidateId, skillId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get())
                .hasFieldOrPropertyWithValue("candidate.id", candidateId)
                .hasFieldOrPropertyWithValue("skill.id", skillId)
                .hasFieldOrPropertyWithValue("proficiencyLevel", "Expert");
    }

    @Test
    void findByCandidateIdAndSkillId_shouldReturnEmptyWhenNotFound() {
        // Given
        Long nonExistentCandidateId = 999L;
        Long skillId = skill.getId();

        // When
        Optional<CandidateSkill> result = candidateSkillRepository.findByCandidateIdAndSkillId(nonExistentCandidateId, skillId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void updateProficiencyLevel_shouldUpdateWhenFound() {
        // Given
        Long candidateId = candidate.getId();
        Long skillId = skill.getId();
        String newProficiencyLevel = "Expert";

        // When
        int updatedRows = candidateSkillRepository.updateProficiencyLevel(candidateId, skillId, newProficiencyLevel);

        // Then
        assertThat(updatedRows).isEqualTo(1);

        Optional<CandidateSkill> updatedSkill = candidateSkillRepository.findByCandidateIdAndSkillId(candidateId, skillId);
        assertThat(updatedSkill).isPresent();
        assertThat(updatedSkill.get().getProficiencyLevel()).isEqualTo(newProficiencyLevel);
    }

    @Test
    void updateProficiencyLevel_shouldReturnZeroWhenNotFound() {
        // Given
        Long nonExistentCandidateId = 999L;
        Long skillId = skill.getId();
        String newProficiencyLevel = "Expert";

        // When
        int updatedRows = candidateSkillRepository.updateProficiencyLevel(nonExistentCandidateId, skillId, newProficiencyLevel);

        // Then
        assertThat(updatedRows).isEqualTo(0);
    }
}
