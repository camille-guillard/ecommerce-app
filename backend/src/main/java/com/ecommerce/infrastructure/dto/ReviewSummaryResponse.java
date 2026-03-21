package com.ecommerce.infrastructure.dto;

import java.util.Map;

public record ReviewSummaryResponse(
        double averageRating,
        long count,
        Map<Integer, Long> histogram
) {
}
