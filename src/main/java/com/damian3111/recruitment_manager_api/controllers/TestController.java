package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.services.JWTService;
import com.damian3111.recruitment_manager_api.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.LoginUserDto;
import org.openapitools.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
public class TestController {

    private final JWTService jwtService;
    private final UserService userService;

    @GetMapping("/test")
    public String helloWorld(){
        return "hello world!";
    }

    @PostMapping("/register")
    public String helloWorld2(@RequestBody User user){
        System.out.println(user);
        System.out.println(user.getEmail());
        System.out.println(user.getFirstName());
        System.out.println(user.getUserRole());
        System.out.println(user.getLastName());
        return "hello world!";
    }

    @PostMapping("/login")
    public ResponseEntity<String> helloWorld22(@RequestBody LoginUserDto loginUserDto, HttpServletResponse response){
        UserEntity userEntity = userService.authenticate(loginUserDto);
        String jwtToken = jwtService.handleLogin(userEntity, response);
        response.setHeader("Set-Cookie", "authToken=" + jwtToken + "; Path=/; Max-Age=86400; HttpOnly; Secure; SameSite=None");
        System.out.println("Set-Cookie header value: " + response.getHeader("Set-Cookie"));

        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/logout2")
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
