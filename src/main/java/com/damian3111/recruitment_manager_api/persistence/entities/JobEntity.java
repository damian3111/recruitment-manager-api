package com.damian3111.recruitment_manager_api.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double salary;

    // 👇 Możliwe przyszłe rozszerzenia:

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "recruiter_id")
    // private UserEntity recruiter;

    // @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<AbilityRequirementEntity> requiredAbilities;
}

