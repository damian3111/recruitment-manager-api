package com.damian3111.recruitment_manager_api.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidate_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(CandidateSkillId.class)
public class CandidateSkill {

    @Id
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private CandidateEntity candidate;

    @Id
    @ManyToOne
    @JoinColumn(name = "skill_id")
    private SkillEntity skill;

    @Column(name = "proficiency_level", nullable = false)
    private String proficiencyLevel;
}
