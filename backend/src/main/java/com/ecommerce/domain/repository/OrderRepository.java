package com.ecommerce.domain.repository;

import com.ecommerce.domain.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    @Query("SELECT DISTINCT o FROM CustomerOrder o LEFT JOIN FETCH o.lines l LEFT JOIN FETCH l.product WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<CustomerOrder> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(ol) > 0 FROM OrderLine ol WHERE ol.order.user.id = :userId AND ol.product.id = :productId")
    boolean existsOrderLineByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
