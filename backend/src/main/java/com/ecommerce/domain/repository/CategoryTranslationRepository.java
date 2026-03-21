package com.ecommerce.domain.repository;

import com.ecommerce.domain.model.CategoryTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryTranslationRepository extends JpaRepository<CategoryTranslation, Long> {

    Optional<CategoryTranslation> findByCategoryIdAndLocale(Long categoryId, String locale);

    List<CategoryTranslation> findByLocale(String locale);
}
