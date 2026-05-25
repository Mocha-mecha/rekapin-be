package com.ut.rekapinbe.controller;

import com.ut.rekapinbe.dto.ApiResponse;
import com.ut.rekapinbe.dto.TransactionRequest;
import com.ut.rekapinbe.entity.Transaction;
import com.ut.rekapinbe.security.CustomUserDetails;
import com.ut.rekapinbe.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> create(@Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Transaction recorded successfully", transactionService.create(request, userDetails.user())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Transaction>>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<Transaction> transactions;
        if (start != null && end != null) {
            transactions = transactionService.getByDateRange(start, end, userDetails.user());
        } else {
            transactions = transactionService.getAll(userDetails.user());
        }
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> getById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transactionService.getById(id, userDetails.user())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        transactionService.delete(id, userDetails.user());
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        transactionService.deleteAll(userDetails.user());
        return ResponseEntity.ok(ApiResponse.success("All transactions deleted successfully", null));
    }
}
