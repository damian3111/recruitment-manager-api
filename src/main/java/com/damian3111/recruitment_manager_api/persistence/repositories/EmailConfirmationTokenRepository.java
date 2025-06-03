package com.damian3111.recruitment_manager_api.persistence.repositories;

import com.damian3111.recruitment_manager_api.persistence.entities.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long>{
    Optional<EmailConfirmationToken> findByToken(String token);
}
