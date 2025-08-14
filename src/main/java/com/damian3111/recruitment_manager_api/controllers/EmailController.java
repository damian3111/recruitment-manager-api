package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.EmailConfirmationToken;
import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.EmailConfirmationTokenRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.EmailApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class EmailController implements EmailApi {

    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<String> confirmEmail(String token) {
        Optional<EmailConfirmationToken> optionalConfirmationToken = emailConfirmationTokenRepository.findByToken(token);

        if (optionalConfirmationToken.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("You've passed incorrect token or your account has already been activated");
        }

        EmailConfirmationToken confirmationToken = optionalConfirmationToken.get();

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        UserEntity user = confirmationToken.getUser();
        user.setEmailConfirmed(true);
        userRepository.save(user);
        emailConfirmationTokenRepository.delete(confirmationToken);

        return ResponseEntity.ok("Your email has been successfully verified");    }
}

