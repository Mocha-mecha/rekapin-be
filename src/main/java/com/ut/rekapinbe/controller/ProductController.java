package com.ut.rekapinbe.controller;

import com.ut.rekapinbe.dto.ApiResponse;
import com.ut.rekapinbe.dto.ProductRequest;
import com.ut.rekapinbe.entity.Product;
import com.ut.rekapinbe.security.CustomUserDetails;
import com.ut.rekapinbe.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", productService.getAll(userDetails.user())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", productService.getById(id, userDetails.user())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@Valid @RequestBody ProductRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Product created successfully", productService.create(request, userDetails.user())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", productService.update(id, request, userDetails.user())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        productService.delete(id, userDetails.user());
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}
