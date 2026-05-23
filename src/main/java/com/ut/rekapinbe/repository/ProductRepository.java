package com.ut.rekapinbe.repository;

import com.ut.rekapinbe.entity.Product;
import com.ut.rekapinbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUser(User user);
    Optional<Product> findByIdAndUser(Long id, User user);
}
