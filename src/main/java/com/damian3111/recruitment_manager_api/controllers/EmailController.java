package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.EmailConfirmationToken;
import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.EmailConfirmationTokenRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.EmailApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class EmailController implements EmailApi {

    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<String> confirmEmail(String token) {
        EmailConfirmationToken confirmationToken = emailConfirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        UserEntity user = confirmationToken.getUser();
        user.setEmailConfirmed(true);
        userRepository.save(user);
        emailConfirmationTokenRepository.delete(confirmationToken);

        return ResponseEntity.ok("Email confirmed");    }
}

