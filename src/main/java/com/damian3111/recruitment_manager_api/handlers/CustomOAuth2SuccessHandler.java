package com.damian3111.recruitment_manager_api.handlers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.services.JWTService;
import com.damian3111.recruitment_manager_api.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@
Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTService jwtService;
    private final UserService userService;

    public CustomOAuth2SuccessHandler(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String principalName = authentication.getName();
        System.out.println("OAuth2 email: {}, Principal name: {}" + email + principalName);
        if (email == null) {
            System.out.println("Email not found in OAuth2 attributes");
            response.sendRedirect("https://damiankwasny.pl/login?error=no_email");
            return;
        }
        try {
            UserEntity userEntity = userService.loadOrCreateUserFromOAuth(email);
            System.out.println("User created/found: {}" +  userEntity.getEmail());
            String jwtToken = jwtService.handleLogin(userEntity, response);
            String encodedToken = URLEncoder.encode(jwtToken, StandardCharsets.UTF_8.toString());
            String redirectUrl = System.getenv("FRONTEND_URL") != null
                    ? System.getenv("FRONTEND_URL") + "/login?token=" + encodedToken
                    : "https://damiankwasny.pl/login?token=" + encodedToken;
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            System.out.println("Error processing OAuth2 login: {}"+ e.getMessage() + e);
            response.sendRedirect("http://localhost:3000/login?error=auth_failed");
        }
    }
}

