package com.ut.rekapinbe.service;

import com.ut.rekapinbe.dto.TransactionRequest;
import com.ut.rekapinbe.entity.Product;
import com.ut.rekapinbe.entity.Transaction;
import com.ut.rekapinbe.entity.TransactionItem;
import com.ut.rekapinbe.entity.User;
import com.ut.rekapinbe.repository.ProductRepository;
import com.ut.rekapinbe.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public Transaction create(TransactionRequest request, User user) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .totalAmount(BigDecimal.ZERO)
                .estimatedProfit(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (TransactionRequest.ItemRequest itemReq : request.items()) {
            Product product = productRepository.findByIdAndUser(itemReq.productId(), user)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.productId()));

            if (Boolean.TRUE.equals(product.getUseStock())) {
                if (product.getStock() == null || product.getStock() < itemReq.quantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getName());
                }
                product.setStock(product.getStock() - itemReq.quantity());
                productRepository.save(product);
            }

            BigDecimal effectiveSellingPrice = product.getSellingPrice();
            if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal multiplier = BigDecimal.ONE.subtract(product.getDiscount().divide(BigDecimal.valueOf(100)));
                effectiveSellingPrice = product.getSellingPrice().multiply(multiplier);
            }

            BigDecimal subtotal = effectiveSellingPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));
            BigDecimal profit = effectiveSellingPrice.subtract(product.getCostPrice())
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));

            TransactionItem item = TransactionItem.builder()
                    .transaction(transaction)
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemReq.quantity())
                    .costPrice(product.getCostPrice())
                    .sellingPrice(effectiveSellingPrice)
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

    public List<Transaction> getAll(User user) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Transaction getById(Long id, User user) {
        return transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public List<Transaction> getByDateRange(LocalDateTime start, LocalDateTime end, User user) {
        return transactionRepository.findByUserAndCreatedAtBetween(user, start, end);
    }

    @Transactional
    public void delete(Long id, User user) {
        Transaction transaction = getById(id, user);
        transactionRepository.delete(transaction);
    }

    @Transactional
    public void deleteAll(User user) {
        List<Transaction> transactions = transactionRepository.findByUser(user);
        transactionRepository.deleteAll(transactions);
    }
}
