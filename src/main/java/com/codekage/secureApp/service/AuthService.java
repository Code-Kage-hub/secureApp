package com.codekage.secureApp.service;

import com.codekage.secureApp.config.JwtService;
import com.codekage.secureApp.dto.AuthResponse;
import com.codekage.secureApp.dto.LoginRequest;
import com.codekage.secureApp.dto.RegisterRequest;
import com.codekage.secureApp.entity.User;
import com.codekage.secureApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtservice;

    public AuthResponse register(RegisterRequest request){

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);

        String jwtToken = jwtservice.generateToken(user);

        return AuthResponse.builder().
                token(jwtToken).
                role(user.getRole()).
                message("User Created Successfully").
                build();
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String jwtToken = jwtservice.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .message("Login Succeed")
                .build();
    }

}
