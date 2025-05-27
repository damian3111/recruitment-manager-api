package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.UserRole;
import com.damian3111.recruitment_manager_api.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.LoginUserDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationProvider authenticationProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserEntity loadOrCreateUserFromOAuth(String email) {
        return userRepository.findUserEntityByEmail(email)
                .orElseGet(() -> {
                    UserEntity user = new UserEntity();
                    user.setEmail(email);
                    user.setRole(UserRole.RECRUITER);
                    user.setPassword("testPassword");
                    user.setEmailConfirmed(true);
                    return userRepository.save(user);
                });
    }

    public UserEntity addUser(UserEntity user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserEntity authenticate(LoginUserDto loginUserDto) {
        try {
            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword())
            );
        }catch (Exception e){
                throw new RuntimeException("Wrong credentials");
        }

        UserEntity userEntity = userRepository.findUserEntityByEmail(loginUserDto.getEmail())
                .orElseThrow();

        if (!userEntity.isEmailConfirmed()) {
            throw new RuntimeException("Please confirm your email before logging in.");
        }

        return userEntity;
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findUserEntityByEmail(email).orElseThrow();
    }
}
