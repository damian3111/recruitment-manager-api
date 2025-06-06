package com.damian3111.recruitment_manager_api.services;

import com.damian3111.recruitment_manager_api.persistence.entities.UserEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.UserRole;
import com.damian3111.recruitment_manager_api.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openapitools.model.LoginUserDto;
import org.openapitools.model.User;
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
    private final ModelMapper modelMapper;
    private final CustomEmailService customEmailService;

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
    public UserEntity addUser(User user) {
        UserEntity build = UserEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(modelMapper.map(user.getUserRole(), UserRole.class))
                .build();

        build.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(build);
        customEmailService.sendConfirmationEmail(build);

        return build;
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
