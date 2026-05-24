package com.ut.rekapinbe.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class TransactionRequest {
    @NotEmpty
    private List<ItemRequest> items;

    @Data
    public static class ItemRequest {
        @NotNull
        private Long productId;

        @NotNull
        @Positive
        private Integer quantity;
    }
}
