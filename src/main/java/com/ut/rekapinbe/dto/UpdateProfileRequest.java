package com.ut.rekapinbe.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
    @NotBlank String fullName,
    @NotBlank String username
) {}
