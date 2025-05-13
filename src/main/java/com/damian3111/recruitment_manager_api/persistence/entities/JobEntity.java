package com.damian3111.recruitment_manager_api.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    @Column(length = 3000)
    private String requirements;

    @Column(length = 3000)
    private String responsibilities;

    @Column(name = "employment_type")
    private String employmentType; // e.g., Full-time, Contract

    @Column(nullable = false)
    private String location;

    @Column(name = "salary_min")
    private BigDecimal salaryMin;

    @Column(name = "salary_max")
    private BigDecimal salaryMax;

    @Column
    private String currency;

    @Column(name = "experience_level")
    private String experienceLevel; // e.g., Junior, Mid, Senior

    @Column
    private String industry;

    @Column(name = "company_name")
    private String companyName;

    @Column(length = 1000)
    private String benefits; // JSON as string or mapped as a separate entity in the future

    @Column(name = "employment_mode")
    private String employmentMode; // e.g., Remote, Hybrid, On-site

    @Column(name = "posted_date")
    private LocalDate postedDate;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "job")
    private List<InvitationEntity> invitationsSent = new ArrayList<>();


}
