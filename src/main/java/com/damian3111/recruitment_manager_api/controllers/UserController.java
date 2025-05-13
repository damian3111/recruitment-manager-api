package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.UserRole;
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

@RequiredArgsConstructor
@RestController
public class UserController implements UsersApi {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<User> addUser(@RequestBody User user) {
        UserEntity build = UserEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(modelMapper.map(user.getUserRole(), UserRole.class))
                .build();

        UserEntity userEntity = userService.addUser(build);

        return ResponseEntity.ok(modelMapper.map(userEntity, User.class));
    }

    @Override
    public ResponseEntity<User> getUserByEmail(String email) {
        UserEntity user = userService.getUserByEmail(email);


        return null;
    }

    @Override
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        return ResponseEntity.ok(modelMapper.map(userService.getUserByEmail(email), User.class));
    }
}
