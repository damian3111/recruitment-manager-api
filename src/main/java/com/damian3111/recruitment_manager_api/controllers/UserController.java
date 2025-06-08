package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.UserRole;
import com.damian3111.recruitment_manager_api.services.CustomEmailService;
import com.damian3111.recruitment_manager_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openapitools.api.UsersApi;
import org.openapitools.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
public class UserController implements UsersApi {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return ResponseEntity.ok(modelMapper.map(userService.addUser(user), User.class));
    }

    @Override
    public ResponseEntity<User> getUserByEmail(String email) {
        return ResponseEntity.ok(modelMapper.map(userService.getUserByEmail(email), User.class));
    }

    @Override
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("Authentication is null or not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String principalName = authentication.getName();
        System.out.println("Principal name: {}" + principalName);
        try {
            UserEntity user = userService.getUserByEmail(principalName);
            return ResponseEntity.ok(modelMapper.map(user, User.class));
        } catch (NoSuchElementException e) {
            System.out.println("User not found for principal: {}" + principalName +", " + e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
