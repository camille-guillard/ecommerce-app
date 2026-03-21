package com.ecommerce.domain.service;

import com.ecommerce.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ProductService {

    Page<Product> getProducts(Long categoryId, String search, Boolean onPromotion, Boolean availableOnly, Boolean preorderOnly, Integer minRating, Map<String, String> attributes, Pageable pageable);

    Product getProductById(Long id);
}
