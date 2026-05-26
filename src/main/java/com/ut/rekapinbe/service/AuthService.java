package com.ut.rekapinbe.service;

import com.ut.rekapinbe.dto.*;
import com.ut.rekapinbe.entity.User;
import com.ut.rekapinbe.repository.UserRepository;
import com.ut.rekapinbe.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .securityQuestion(request.securityQuestion())
                .securityAnswer(request.securityAnswer() == null ? null : passwordEncoder.encode(request.securityAnswer().trim().toLowerCase()))
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (AuthenticationException ex) {
            throw new RuntimeException("Username atau password salah silahkan coba lagi");
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponse(token);
    }

    @Transactional
    public void delete(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toProfile(user);
    }

    @Transactional
    public ProfileUpdateResponse updateProfile(String currentUsername, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.findByUsername(request.username())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> { throw new RuntimeException("Username already exists"); });

        user.setFullName(request.fullName());
        user.setUsername(request.username());
        User saved = userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getUsername());
        String token = jwtUtils.generateToken(userDetails);
        return new ProfileUpdateResponse(token, toProfile(saved));
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void saveSecurityQuestion(String username, SecurityQuestionRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setSecurityQuestion(request.securityQuestion());
        user.setSecurityAnswer(passwordEncoder.encode(request.securityAnswer().trim().toLowerCase()));
        userRepository.save(user);
    }

    public SecurityQuestionResponse getSecurityQuestion(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getSecurityQuestion() == null || user.getSecurityQuestion().isBlank()) {
            throw new RuntimeException("This account does not have a security question");
        }
        return new SecurityQuestionResponse(user.getSecurityQuestion());
    }

    public void verifySecurityAnswer(VerifySecurityAnswerRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean questionMatches = user.getSecurityQuestion() != null
                && user.getSecurityQuestion().equals(request.securityQuestion());
        boolean answerMatches = user.getSecurityAnswer() != null
                && passwordEncoder.matches(request.answer().trim().toLowerCase(), user.getSecurityAnswer());

        if (!questionMatches || !answerMatches) {
            throw new RuntimeException("Pertanyaan keamanan atau jawaban tidak sesuai");
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private UserProfileResponse toProfile(User user) {
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getFullName(), user.getSecurityQuestion());
    }
}
