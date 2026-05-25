package com.ut.rekapinbe.service;

import com.ut.rekapinbe.dto.ProductRequest;
import com.ut.rekapinbe.entity.Product;
import com.ut.rekapinbe.entity.User;
import com.ut.rekapinbe.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAll(User user) {
        return productRepository.findByUser(user);
    }

    public Product getById(Long id, User user) {
        return productRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public Product create(ProductRequest request, User user) {
        Product product = Product.builder()
                .name(request.name())
                .costPrice(request.costPrice())
                .sellingPrice(request.sellingPrice())
                .stock(request.stock())
                .useStock(request.useStock() != null && request.useStock())
                .user(user)
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequest request, User user) {
        Product product = getById(id, user);
        product.setName(request.name());
        product.setCostPrice(request.costPrice());
        product.setSellingPrice(request.sellingPrice());
        product.setStock(request.stock());
        product.setUseStock(request.useStock());
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id, User user) {
        Product product = getById(id, user);
        productRepository.delete(product);
    }
}
