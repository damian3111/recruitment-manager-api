package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.EmailConfirmationToken;
import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.EmailConfirmationTokenRepository;
import com.damian3111.recruitment_manager_api.persistence.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class CustomEmailService {

    private final JavaMailSender mailSender;
    private final EmailConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(CustomEmailService.class);

    @Async
    public CompletableFuture<Void> sendConfirmationEmail(UserEntity user) {
        if (user.isEmailConfirmed()) {
            return CompletableFuture.completedFuture(null);
        }

        String token = UUID.randomUUID().toString();
        EmailConfirmationToken confirmationToken = new EmailConfirmationToken();
        confirmationToken.setToken(token);
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        confirmationToken.setUser(user);

        confirmationTokenRepository.save(confirmationToken);

        String link = "https://java-application-uo30.onrender.com/email/confirm-email?token=" + token;

        try {
            sendHtmlEmail(
                    user.getEmail(),
                    "Confirm your email",
                    buildHtmlEmail(user.getFirstName(), link)
            );
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send confirmation email to {}: {}", user.getEmail(), e.getMessage());
            return CompletableFuture.failedFuture(new RuntimeException("Failed to send email", e));
        }
    }
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public void confirmEmail(String token) {
        EmailConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        UserEntity user = confirmationToken.getUser();
        user.setEmailConfirmed(true);
        userRepository.save(user);
        confirmationTokenRepository.delete(confirmationToken);
    }

    private String buildHtmlEmail(String name, String link) {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Email Confirmation</title>
        </head>
        <body style="margin: 0; padding: 0; background-color: #f4f7fa; font-family: 'Helvetica Neue', Arial, sans-serif;">
            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background: linear-gradient(180deg, #e8f0fe 0%%, #f4f7fa 100%%);">
                <tr>
                    <td align="center" style="padding: 40px 20px;">
                        <table role="presentation" width="100%%" style="max-width: 600px; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); overflow: hidden;">
                            <!-- Header -->
                            <tr>
                                <td style="background-color: #2c3e50; padding: 20px; text-align: center;">
                                    <h1 style="color: #ffffff; font-size: 24px; margin: 0; font-weight: 500;">Welcome to Recruitment Manager</h1>
                                </td>
                            </tr>
                            <!-- Content -->
                            <tr>
                                <td style="padding: 40px 30px; color: #333333;">
                                    <h2 style="font-size: 20px; color: #2c3e50; margin-top: 0;">Hello %s,</h2>
                                    <p style="font-size: 16px; line-height: 1.6; margin: 0 0 20px;">
                                        Thank you for registering with Recruitment Manager. We're excited to have you on board!
                                    </p>
                                    <p style="font-size: 16px; line-height: 1.6; margin: 0 0 20px;">
                                        Please confirm your email address by clicking the button below to activate your account:
                                    </p>
                                    <table role="presentation" cellspacing="0" cellpadding="0" style="margin: 20px auto;">
                                        <tr>
                                            <td style="border-radius: 8px; background-color: #4CAF50;">
                                                <a href="%s" style="display: inline-block; padding: 14px 32px; color: #ffffff; font-size: 16px; font-weight: 500; text-decoration: none; border-radius: 8px;">
                                                    Confirm Your Email
                                                </a>
                                            </td>
                                        </tr>
                                    </table>
                                    <p style="font-size: 14px; line-height: 1.6; color: #666666; margin: 20px 0 0;">
                                        This link will expire in 15 minutes for security reasons.
                                    </p>
                                    <p style="font-size: 14px; line-height: 1.6; color: #666666; margin: 10px 0;">
                                        If you didn’t request this email, please ignore it or contact our support team.
                                    </p>
                                </td>
                            </tr>
                            <!-- Footer -->
                            <tr>
                                <td style="background-color: #f8fafc; padding: 20px; text-align: center; font-size: 12px; color: #888888; border-top: 1px solid #e8ecef;">
                                    <p style="margin: 0;">&copy; %d Recruitment Manager. All rights reserved.</p>
                                    <p style="margin: 5px 0 0;">
                                        <a href="https://yourwebsite.com/support" style="color: #4CAF50; text-decoration: none;">Contact Support</a> | 
                                        <a href="https://yourwebsite.com/privacy" style="color: #4CAF50; text-decoration: none;">Privacy Policy</a>
                                    </p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """.formatted(name != null ? name : "there", link, LocalDateTime.now().getYear());
    }
}
