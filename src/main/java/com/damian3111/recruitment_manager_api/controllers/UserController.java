package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.UserRole;
import com.damian3111.recruitment_manager_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openapitools.api.UsersApi;
import org.openapitools.model.User;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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

    //    @Override
//    public ResponseEntity<User> addUser(User user) {
//        System.out.println("@@@@@@@@@@@@@@@");
//        return ResponseEntity.ok(new User());
//    }
}
