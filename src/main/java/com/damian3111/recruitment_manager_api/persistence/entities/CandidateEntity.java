package com.damian3111.recruitment_manager_api.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    private String phone;

    private String profilePictureUrl;

    private String headline;

    private String summary;

    private String experience;

    private Integer yearsOfExperience;

    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(columnDefinition = "TEXT")
    private String workExperiences;

    @Column(columnDefinition = "TEXT")
    private String projects;

    private String mediaUrl;

    private String salaryExpectation;

    private String workStyle;

    private LocalDate appliedDate;

    private String location;

    @Column(length = 2000)
    private String skills; // store as comma-separated string or convert to JSON string

    @OneToMany(mappedBy = "candidate")
    private List<InvitationEntity> invitationsReceived = new ArrayList<>();
}
