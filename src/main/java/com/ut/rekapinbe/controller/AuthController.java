package com.ut.rekapinbe.controller;

import com.ut.rekapinbe.dto.ApiResponse;
import com.ut.rekapinbe.dto.AuthResponse;
import com.ut.rekapinbe.dto.LoginRequest;
import com.ut.rekapinbe.dto.RegisterRequest;
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

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal UserDetails userDetails) {
        authService.delete(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
}
