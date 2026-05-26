package com.ut.rekapinbe.dto;

import jakarta.validation.constraints.NotBlank;

public record SecurityQuestionRequest(
    @NotBlank String securityQuestion,
    @NotBlank String securityAnswer
) {}
