package com.ut.rekapinbe.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifySecurityAnswerRequest(
    @NotBlank String username,
    @NotBlank String securityQuestion,
    @NotBlank String answer
) {}
