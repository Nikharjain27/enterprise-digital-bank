package com.bank.auth.service.impl;

import com.bank.auth.dto.*;
import com.bank.auth.entity.Role;
import com.bank.auth.entity.User;
import com.bank.auth.enums.RoleType;
import com.bank.auth.repository.RoleRepository;
import com.bank.auth.repository.UserRepository;
import com.bank.auth.security.JwtUtil;
import com.bank.auth.service.AuthService;
import com.bank.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException(
                    "USERNAME_EXISTS",
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BaseException(
                    "EMAIL_EXISTS",
                    "Email already exists"
            );
        }

        Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new BaseException(
                        "ROLE_NOT_FOUND",
                        "Default role not found"
                ));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(customerRole))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .message("User registered successfully")
                .token(null)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token =
                jwtUtil.generateToken(request.getUsername());

        return AuthResponse.builder()
                .message("Login successful")
                .token(token)
                .build();
    }
}