package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.services.JWTService;
import com.damian3111.recruitment_manager_api.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.LoginUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JWTService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserDto loginUserDto, HttpServletResponse response){
        UserEntity userEntity = userService.authenticate(loginUserDto);
        String jwtToken = jwtService.handleLogin(userEntity, response);
        response.setHeader("Set-Cookie", "authToken=" + jwtToken + "; Path=/; Max-Age=3600; HttpOnly; Secure; SameSite=None");

        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("authToken", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok("Logged out successfully");
    }
}
