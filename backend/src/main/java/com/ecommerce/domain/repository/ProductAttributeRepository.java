package com.ecommerce.domain.repository;

import com.ecommerce.domain.model.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    @Query("SELECT DISTINCT pa.attributeKey, pa.attributeValue FROM ProductAttribute pa " +
           "WHERE pa.product.category.id IN :categoryIds ORDER BY pa.attributeKey, pa.attributeValue")
    List<Object[]> findDistinctAttributesByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    List<ProductAttribute> findByProductId(Long productId);
}
