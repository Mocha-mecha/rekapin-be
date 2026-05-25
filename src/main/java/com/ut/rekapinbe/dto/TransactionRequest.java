package com.ut.rekapinbe.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record TransactionRequest(
    @NotEmpty List<ItemRequest> items
) {
    public record ItemRequest(
        @NotNull Long productId,
        @NotNull Integer quantity
    ) {}
}
