package com.ut.rekapinbe.service;

import com.ut.rekapinbe.dto.DashboardResponse;
import com.ut.rekapinbe.entity.Transaction;
import com.ut.rekapinbe.entity.TransactionItem;
import com.ut.rekapinbe.entity.User;
import com.ut.rekapinbe.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardResponse getSummary(User user) {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<Transaction> todayTransactions = transactionRepository.findByUserAndCreatedAtBetween(user, startOfDay, endOfDay);

        BigDecimal totalSales = todayTransactions.stream()
                .map(Transaction::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfit = todayTransactions.stream()
                .map(Transaction::getEstimatedProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Transaction> allTransactions = transactionRepository.findByUserOrderByCreatedAtDesc(user);
        
        Map<String, Long> productSalesMap = allTransactions.stream()
                .flatMap(t -> t.getItems().stream())
                .collect(Collectors.groupingBy(
                        TransactionItem::getProductName,
                        Collectors.summingLong(TransactionItem::getQuantity)
                ));

        List<DashboardResponse.TopProduct> topProducts = productSalesMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new DashboardResponse.TopProduct(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new DashboardResponse(
                totalSales,
                totalProfit,
                todayTransactions.size(),
                topProducts
        );
    }
}
