package com.ut.rekapinbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank String name,
    String category,
    String unit,
    @NotNull BigDecimal costPrice,
    @NotNull BigDecimal sellingPrice,
    BigDecimal discount,
    Integer stock,
    Boolean useStock
) {}
