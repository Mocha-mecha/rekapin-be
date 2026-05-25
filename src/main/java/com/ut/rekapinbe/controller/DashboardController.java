package com.ut.rekapinbe.controller;

import com.ut.rekapinbe.dto.ApiResponse;
import com.ut.rekapinbe.dto.DashboardResponse;
import com.ut.rekapinbe.security.CustomUserDetails;
import com.ut.rekapinbe.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved successfully", dashboardService.getSummary(userDetails.user())));
    }
}
