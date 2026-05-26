package com.ut.rekapinbe.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @NotBlank String username,
    @NotBlank String newPassword
) {}
