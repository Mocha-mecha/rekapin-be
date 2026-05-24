package com.ut.rekapinbe.service;

import com.ut.rekapinbe.dto.DashboardResponse;
import com.ut.rekapinbe.entity.Transaction;
import com.ut.rekapinbe.entity.TransactionItem;
import com.ut.rekapinbe.entity.User;
import com.ut.rekapinbe.repository.TransactionRepository;
import com.ut.rekapinbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public DashboardResponse getDashboardData() {
        User user = getCurrentUser();
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<Transaction> todayTransactions = transactionRepository.findByUserAndCreatedAtBetween(user, startOfDay, endOfDay);

        BigDecimal totalSales = todayTransactions.stream()
                .map(Transaction::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfit = todayTransactions.stream()
                .map(Transaction::getEstimatedProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate top selling products from all time or today? 
        // Dashboard usually implies recent or overall. The requirement says "produk yang paling sering terjual" in dashboard summary.
        // Let's calculate from all transactions for "most frequent", or today for "today's summary". 
        // The issue description says "produk yang paling sering terjual" under Dashboard summary.
        
        List<Transaction> allTransactions = transactionRepository.findByUserOrderByCreatedAtDesc(user);
        
        Map<String, Long> productSalesMap = allTransactions.stream()
                .flatMap(t -> t.getItems().stream())
                .collect(Collectors.groupingBy(
                        TransactionItem::getProductName,
                        Collectors.summingLong(TransactionItem::getQuantity)
                ));

        List<DashboardResponse.ProductSalesDTO> topProducts = productSalesMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> DashboardResponse.ProductSalesDTO.builder()
                        .productName(e.getKey())
                        .quantitySold(e.getValue())
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalSalesToday(totalSales)
                .totalProfitToday(totalProfit)
                .transactionCountToday((long) todayTransactions.size())
                .topSellingProducts(topProducts)
                .build();
    }
}
