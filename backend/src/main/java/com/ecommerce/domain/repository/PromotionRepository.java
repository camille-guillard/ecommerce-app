package com.ecommerce.domain.repository;

import com.ecommerce.domain.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("SELECT p FROM Promotion p WHERE p.product.id = :productId " +
           "AND p.startDate <= :today AND (p.endDate IS NULL OR p.endDate >= :today)")
    Optional<Promotion> findActiveByProductId(@Param("productId") Long productId, @Param("today") LocalDate today);

    @Query("SELECT p FROM Promotion p WHERE p.startDate <= :today AND (p.endDate IS NULL OR p.endDate >= :today)")
    List<Promotion> findAllActive(@Param("today") LocalDate today);

    Optional<Promotion> findByProductId(Long productId);

    void deleteByProductId(Long productId);
}
