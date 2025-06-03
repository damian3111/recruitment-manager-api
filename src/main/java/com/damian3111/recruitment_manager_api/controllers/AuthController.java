package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.services.JWTService;
import com.damian3111.recruitment_manager_api.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.LoginUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTService jwtService;
    private final UserService userService;
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserDto loginUserDto, HttpServletResponse response) {
        if (secretKey == null || secretKey.isEmpty()) {
            System.err.println("JWT_SECRET is not set");
            return ResponseEntity.status(500).body("Server configuration error");
        }

        UserEntity userEntity = userService.authenticate(loginUserDto);
        if (userEntity == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String jwtToken = jwtService.handleLogin(userEntity, response);
        return ResponseEntity.ok()
                .header("Set-Cookie", "authToken=" + jwtToken + "; HttpOnly; Secure; Path=/; Max-Age=86400; SameSite=None")
                .body(jwtToken);
    }

}
