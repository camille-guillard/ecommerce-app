package com.ecommerce.domain.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "category_translations", uniqueConstraints = @UniqueConstraint(columnNames = {"category_id", "locale"}))
@Getter
@Setter
public class CategoryTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 5)
    private String locale;

    @Column(nullable = false)
    private String displayName;

    public CategoryTranslation() {}

    public CategoryTranslation(Category category, String locale, String displayName) {
        this.category = category;
        this.locale = locale;
        this.displayName = displayName;
    }

}
