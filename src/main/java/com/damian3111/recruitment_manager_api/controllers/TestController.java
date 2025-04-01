package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.services.JWTService;
import com.damian3111.recruitment_manager_api.services.UserService;
import jakarta.servlet.http.Cookie;
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
    
    @GetMapping("api/test")
    public String helloWorld(){
        return "hello world!";
    }

    @PostMapping("/api/register")
    public String helloWorld2(@RequestBody User user){
        System.out.println(user);
        System.out.println(user.getEmail());
        System.out.println(user.getFirstName());
        System.out.println(user.getUserRole());
        System.out.println(user.getLastName());
        return "hello world!";
    }

    @PostMapping("/api/login")
    public ResponseEntity<String> helloWorld22(@RequestBody LoginUserDto loginUserDto, HttpServletResponse response){
        UserEntity userEntity = userService.authenticate(loginUserDto);
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", userEntity.getRole());
        String jwtToken = jwtService.generateToken(claims, userEntity);


        Cookie cookie = new Cookie("authToken", jwtToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);

        response.addCookie(cookie);

        return ResponseEntity.ok(jwtToken);
    }
}
