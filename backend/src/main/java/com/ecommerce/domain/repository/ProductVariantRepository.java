package com.ecommerce.domain.repository;

import com.ecommerce.domain.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductId(Long productId);

    Optional<ProductVariant> findByProductIdAndColorAndSize(Long productId, String color, String size);

    boolean existsByProductId(Long productId);

    List<ProductVariant> findByProductIdIn(List<Long> productIds);
}
