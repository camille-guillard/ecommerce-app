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
@Table(name = "product_translations", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "locale"}))
@Getter
@Setter
public class ProductTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 5)
    private String locale;

    @Column(nullable = false)
    private String displayName;

    @Column(length = 500)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String detailedDescription;

    public ProductTranslation() {}

    public ProductTranslation(Product product, String locale, String displayName, String description, String detailedDescription) {
        this.product = product;
        this.locale = locale;
        this.displayName = displayName;
        this.description = description;
        this.detailedDescription = detailedDescription;
    }

}
