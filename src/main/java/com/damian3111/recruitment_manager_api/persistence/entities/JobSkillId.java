package com.damian3111.recruitment_manager_api.persistence.entities;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSkillId implements Serializable {
    private Long job;
    private Long skill;
}
