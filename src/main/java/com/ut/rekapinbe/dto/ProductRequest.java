package com.ut.rekapinbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank String name,
    @NotNull BigDecimal costPrice,
    @NotNull BigDecimal sellingPrice,
    Integer stock,
    Boolean useStock
) {}
