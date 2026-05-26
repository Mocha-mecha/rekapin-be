package com.ut.rekapinbe.controller;

import com.ut.rekapinbe.dto.*;
import com.ut.rekapinbe.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", authService.getProfile(userDetails.getUsername())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileUpdateResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", authService.updateProfile(userDetails.getUsername(), request)));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
    }

    @PutMapping("/security")
    public ResponseEntity<ApiResponse<Void>> saveSecurityQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SecurityQuestionRequest request
    ) {
        authService.saveSecurityQuestion(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Security question updated successfully", null));
    }

    @GetMapping("/security/{username}")
    public ResponseEntity<ApiResponse<SecurityQuestionResponse>> getSecurityQuestion(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.success("Security question retrieved successfully", authService.getSecurityQuestion(username)));
    }

    @PostMapping("/verify-answer")
    public ResponseEntity<ApiResponse<Void>> verifySecurityAnswer(@Valid @RequestBody VerifySecurityAnswerRequest request) {
        authService.verifySecurityAnswer(request);
        return ResponseEntity.ok(ApiResponse.success("Security answer verified successfully", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal UserDetails userDetails) {
        authService.delete(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
}
