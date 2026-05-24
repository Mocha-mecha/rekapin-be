package com.ut.rekapinbe.repository;

import com.ut.rekapinbe.entity.Transaction;
import com.ut.rekapinbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByCreatedAtDesc(User user);
    Optional<Transaction> findByIdAndUser(Long id, User user);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.createdAt BETWEEN :start AND :end ORDER BY t.createdAt DESC")
    List<Transaction> findByUserAndCreatedAtBetween(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
