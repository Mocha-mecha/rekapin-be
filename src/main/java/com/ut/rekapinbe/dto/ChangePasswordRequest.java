package com.ut.rekapinbe.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @NotBlank String oldPassword,
    @NotBlank String newPassword
) {}
