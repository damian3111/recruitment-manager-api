package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.SkillEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional
    public SkillEntity findOrCreateSkill(String name) {
        return skillRepository.findByName(name)
                .orElseGet(() -> {
                    SkillEntity newSkill = SkillEntity.builder()
                            .name(name)
                            .createdAt(LocalDate.now())
                            .build();
                    return skillRepository.save(newSkill);
                });
    }

    public SkillEntity findById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + id));
    }

    public SkillEntity findByName(String name) {
        return skillRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with name: " + name));
    }
}
