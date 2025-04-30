package com.damian3111.recruitment_manager_api.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(JobSkillId.class)
public class JobSkill {

    @Id
    @ManyToOne
    @JoinColumn(name = "job_id")
    private JobEntity job;

    @Id
    @ManyToOne
    @JoinColumn(name = "skill_id")
    private SkillEntity skill;

    @Column(name = "proficiency_level", nullable = false)
    private String proficiencyLevel;
}
