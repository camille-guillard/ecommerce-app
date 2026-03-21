package com.ecommerce.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(int rating, @NotBlank @Size(min = 1, max = 2000) String comment) {
}
