package com.ut.rekapinbe.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal totalSalesToday;
    private BigDecimal totalProfitToday;
    private Long transactionCountToday;
    private List<ProductSalesDTO> topSellingProducts;

    @Data
    @Builder
    public static class ProductSalesDTO {
        private String productName;
        private Long quantitySold;
    }
}
