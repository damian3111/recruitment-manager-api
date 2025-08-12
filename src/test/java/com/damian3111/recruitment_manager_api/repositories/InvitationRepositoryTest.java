package com.damian3111.recruitment_manager_api.repositories;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.InvitationEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.InvitationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.InvitationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InvitationRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private JpaRepository<UserEntity, Long> userRepository;

    @Autowired
    private JpaRepository<CandidateEntity, Long> candidateRepository;

    @Autowired
    private JpaRepository<JobEntity, Long> jobRepository;

    private UserEntity recruiter;
    private UserEntity jobOwner;
    private CandidateEntity candidate;
    private JobEntity job;
    private InvitationEntity invitation;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        invitationRepository.deleteAll();
        jobRepository.deleteAll();
        candidateRepository.deleteAll();
        userRepository.deleteAll();

        recruiter = UserEntity.builder()
                .email("recruiter@example.com")
                .firstName("Recruiter")
                .password("dDslvjUe32*^")
                .build();
        userRepository.save(recruiter);

        jobOwner = UserEntity.builder()
                .email("owner@example.com")
                .firstName("Job Owner")
                .password("dDslvjUe32*^")
                .build();
        userRepository.save(jobOwner);

        candidate = CandidateEntity.builder()
                .email("candidate@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        candidateRepository.save(candidate);

        job = JobEntity.builder()
                .title("Software Engineer")
                .user(jobOwner)
                .location("Washington")
                .build();
        jobRepository.save(job);

        invitation = InvitationEntity.builder()
                .recruiter(recruiter)
                .candidate(candidate)
                .job(job)
                .status(InvitationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
        invitationRepository.save(invitation);
    }

    @Test
    void findByCandidate_IdAndRecruiter_Id_shouldReturnInvitationsWhenFound() {
        // Given
        Long candidateId = candidate.getId();
        Long recruiterId = recruiter.getId();

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidate_IdAndRecruiter_Id(candidateId, recruiterId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get().get(0))
                .hasFieldOrPropertyWithValue("candidate.id", candidateId)
                .hasFieldOrPropertyWithValue("recruiter.id", recruiterId)
                .hasFieldOrPropertyWithValue("status", InvitationStatus.SENT);
    }

    @Test
    void findByCandidate_IdAndRecruiter_Id_shouldReturnEmptyWhenNotFound() {
        // Given
        Long nonExistentCandidateId = 999L;
        Long recruiterId = recruiter.getId();

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidate_IdAndRecruiter_Id(nonExistentCandidateId, recruiterId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    void findByRecruiter_Id_shouldReturnInvitationsWhenFound() {
        // Given
        Long recruiterId = recruiter.getId();

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByRecruiter_Id(recruiterId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get().get(0))
                .hasFieldOrPropertyWithValue("recruiter.id", recruiterId)
                .hasFieldOrPropertyWithValue("status", InvitationStatus.SENT);
    }

    @Test
    void findByRecruiter_Id_shouldReturnEmptyWhenNotFound() {
        // Given
        Long nonExistentRecruiterId = 999L;

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByRecruiter_Id(nonExistentRecruiterId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    void findByJobUserId_shouldReturnInvitationsWhenFound() {
        // Given
        Long userId = Long.MAX_VALUE;
        String email = jobOwner.getEmail();

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByJobUserId(userId, email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get().get(0))
                .hasFieldOrPropertyWithValue("job.user.email", email)
                .satisfies(inv -> assertThat(inv.getRecruiter().getId()).isNotEqualTo(inv.getJob().getUser().getId()));
    }

    @Test
    void findByJobUserId_shouldReturnEmptyWhenNotFound() {
        // Given
        Long userId = recruiter.getId();
        String email = jobOwner.getEmail();

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByJobUserId(userId, email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    void findByCandidateEmailAndRecruiterId_shouldReturnInvitationsWhenFound() {
        // Given
        Long userId = jobOwner.getId();
        Long recruiterId = recruiter.getId();
        String email = candidate.getEmail();

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidateEmailAndRecruiterId(userId, email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get().get(0))
                .hasFieldOrPropertyWithValue("candidate.email", email)
                .hasFieldOrPropertyWithValue("recruiter.id", recruiterId).isNotEqualTo(userId);
    }

    @Test
    void findByCandidateEmailAndRecruiterId_shouldReturnEmptyWhenNotFound() {
        // Given
        Long userId = jobOwner.getId();
        String nonExistentEmail = "nonexistent@example.com";

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidateEmailAndRecruiterId(userId, nonExistentEmail);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    void findByCandidate2_shouldReturnInvitationsWhenFound() {
        // Given
        String email = candidate.getEmail();
        InvitationStatus status = InvitationStatus.SENT;

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidate2(recruiter.getId(), email, status);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get().get(0))
                .hasFieldOrPropertyWithValue("candidate.email", email)
                .hasFieldOrPropertyWithValue("status", status);
    }

    @Test
    void findByCandidate2_shouldReturnEmptyWhenNoMatchingStatus() {
        // Given
        String email = candidate.getEmail();
        InvitationStatus nonMatchingStatus = InvitationStatus.ACCEPTED;

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidate2(recruiter.getId(), email, nonMatchingStatus);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    void findByCandidateEmailOrJobUserEmail_shouldReturnInvitationsWhenFound() {
        // Given
        String email = candidate.getEmail();

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidateEmailOrJobUserEmail(recruiter.getId(), email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get().get(0))
                .satisfies(inv -> {
                    assertThat(inv.getCandidate().getEmail().equals(email) ||
                            inv.getJob().getUser().getEmail().equals(email)).isTrue();
                });
    }

    @Test
    void findByCandidateEmailOrJobUserEmail_shouldReturnEmptyWhenNotFound() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidateEmailOrJobUserEmail(recruiter.getId(), nonExistentEmail);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    void findByCandidateIdAndJobId_shouldReturnInvitationsWhenFound() {
        // Given
        Long candidateId = candidate.getId();
        Long jobId = job.getId();

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidateIdAndJobId(candidateId, jobId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get().get(0))
                .hasFieldOrPropertyWithValue("candidate.id", candidateId)
                .hasFieldOrPropertyWithValue("job.id", jobId);
    }

    @Test
    void findByCandidateIdAndJobId_shouldReturnEmptyWhenNotFound() {
        // Given
        Long nonExistentCandidateId = 999L;
        Long jobId = job.getId();

        // When
        Optional<List<InvitationEntity>> result = invitationRepository.findByCandidateIdAndJobId(nonExistentCandidateId, jobId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    void updateStatusById_shouldUpdateStatusWhenFound() {
        // Given
        Long invitationId = invitation.getId();
        InvitationStatus newStatus = InvitationStatus.SENT;

        // When
        int updatedRows = invitationRepository.updateStatusById(invitationId, newStatus);

        // Then
        assertThat(updatedRows).isEqualTo(1);
        Optional<InvitationEntity> updatedInvitation = invitationRepository.findById(invitationId);
        assertThat(updatedInvitation).isPresent();
        assertThat(updatedInvitation.get().getStatus()).isEqualTo(newStatus);
    }

    @Test
    void updateStatusById_shouldReturnZeroWhenNotFound() {
        // Given
        Long nonExistentId = 999L;
        InvitationStatus newStatus = InvitationStatus.ACCEPTED;

        // When
        int updatedRows = invitationRepository.updateStatusById(nonExistentId, newStatus);

        // Then
        assertThat(updatedRows).isEqualTo(0);
    }
}