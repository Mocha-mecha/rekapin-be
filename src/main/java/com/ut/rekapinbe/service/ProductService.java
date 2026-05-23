package com.ut.rekapinbe.service;

import com.ut.rekapinbe.dto.ProductRequest;
import com.ut.rekapinbe.entity.Product;
import com.ut.rekapinbe.entity.User;
import com.ut.rekapinbe.repository.ProductRepository;
import com.ut.rekapinbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Product> getAllProducts() {
        return productRepository.findByUser(getCurrentUser());
    }

    public Product getProductById(Long id) {
        return productRepository.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .costPrice(request.getCostPrice())
                .sellingPrice(request.getSellingPrice())
                .stock(request.getStock())
                .useStock(request.getUseStock())
                .user(getCurrentUser())
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProductById(id);
        product.setName(request.getName());
        product.setCostPrice(request.getCostPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setStock(request.getStock());
        product.setUseStock(request.getUseStock());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
