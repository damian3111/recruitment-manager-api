package com.damian3111.recruitment_manager_api.handlers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.services.JWTService;
import com.damian3111.recruitment_manager_api.services.UserService;
import jakarta.servlet.http.Cookie;
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

        if (email == null) {
            response.sendRedirect("https://damiankwasny.pl/login?error=no_email");
            return;
        }

        try {
            UserEntity userEntity = userService.loadOrCreateUserFromOAuth(email);
            String jwtToken = jwtService.handleLogin(userEntity, response);

            // Set JWT token in a secure, HttpOnly cookie
            Cookie cookie = new Cookie("authToken", jwtToken);
            cookie.setHttpOnly(true); // Prevents JavaScript access
            cookie.setSecure(true); // Requires HTTPS
            cookie.setPath("/"); // Available for all paths
            cookie.setMaxAge(3600); // 1 hour expiry
            cookie.setAttribute("SameSite", "None"); // Required for cross-site redirects
            response.addCookie(cookie);

            // Redirect to frontend without token in URL
            response.sendRedirect("https://damiankwasny.pl/login?success=true");
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            response.sendRedirect("https://damiankwasny.pl/login?error=auth_failed");
        }
    }}

