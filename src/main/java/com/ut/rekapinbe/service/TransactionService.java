package com.ut.rekapinbe.service;

import com.ut.rekapinbe.dto.TransactionRequest;
import com.ut.rekapinbe.entity.Product;
import com.ut.rekapinbe.entity.Transaction;
import com.ut.rekapinbe.entity.TransactionItem;
import com.ut.rekapinbe.entity.User;
import com.ut.rekapinbe.repository.ProductRepository;
import com.ut.rekapinbe.repository.TransactionRepository;
import com.ut.rekapinbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public Transaction createTransaction(TransactionRequest request) {
        User user = getCurrentUser();
        Transaction transaction = Transaction.builder()
                .user(user)
                .totalAmount(BigDecimal.ZERO)
                .estimatedProfit(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (TransactionRequest.ItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findByIdAndUser(itemReq.getProductId(), user)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

            if (Boolean.TRUE.equals(product.getUseStock())) {
                if (product.getStock() == null || product.getStock() < itemReq.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getName());
                }
                product.setStock(product.getStock() - itemReq.getQuantity());
                productRepository.save(product);
            }

            BigDecimal subtotal = product.getSellingPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            BigDecimal profit = product.getSellingPrice().subtract(product.getCostPrice())
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            TransactionItem item = TransactionItem.builder()
                    .transaction(transaction)
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemReq.getQuantity())
                    .costPrice(product.getCostPrice())
                    .sellingPrice(product.getSellingPrice())
                    .subtotal(subtotal)
                    .build();

            transaction.getItems().add(item);
            totalAmount = totalAmount.add(subtotal);
            totalProfit = totalProfit.add(profit);
        }

        transaction.setTotalAmount(totalAmount);
        transaction.setEstimatedProfit(totalProfit);

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findByUserOrderByCreatedAtDesc(getCurrentUser());
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByUserAndCreatedAtBetween(getCurrentUser(), start, end);
    }
}
