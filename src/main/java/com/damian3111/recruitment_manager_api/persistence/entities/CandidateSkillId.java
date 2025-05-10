package com.damian3111.recruitment_manager_api.persistence.entities;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSkillId implements Serializable {
    private Long candidate;
    private Long skill;
}