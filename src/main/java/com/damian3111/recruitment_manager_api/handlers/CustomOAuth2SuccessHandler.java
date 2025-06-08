package com.damian3111.recruitment_manager_api.handlers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.services.JWTService;
import com.damian3111.recruitment_manager_api.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

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
        UserEntity userEntity = userService.loadOrCreateUserFromOAuth(email);
        String jwtToken = jwtService.handleLogin(userEntity, response);

        String encodedToken = URLEncoder.encode(jwtToken, StandardCharsets.UTF_8.toString());

        String redirectUrl = "https://damiankwasny.pl/login?token=" + jwtToken;
//        response.sendRedirect(redirectUrl);




        ResponseCookie cookie = ResponseCookie.from("authToken", jwtToken)
                .httpOnly(true)
                .secure(true) // Make sure you're using HTTPS
                .path("/")
                .sameSite("None") // Required for cross-site cookies
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(redirectUrl);
    }
}

