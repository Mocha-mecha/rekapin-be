package com.ut.rekapinbe.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
    BigDecimal totalSales,
    BigDecimal totalProfit,
    long transactionCount,
    List<TopProduct> topProducts
) {
    public record TopProduct(
        String name,
        long quantity
    ) {}
}
