package com.ut.rekapinbe.dto;

public record UserProfileResponse(
    Long id,
    String username,
    String fullName,
    String securityQuestion
) {}
