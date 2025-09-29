package com.hansaflex.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductFilterRequest {

    private String region;
    private String category;
    private List<String> categories; // Multiple categories support
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String name;
    private Integer minStock; // Minimum stock quantity
    private Integer maxStock; // Maximum stock quantity
    private String currency; // Filter by currency
    private Boolean inStock; // Only products with stock > 0
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 10;
    @Builder.Default
    private String sortBy = "name";
    @Builder.Default
    private String sortDirection = "asc";

    /**
     * Custom toString method for cache key generation
     * Includes all filter parameters to ensure unique cache keys
     */
    @Override
    public String toString() {
        return String.format("ProductFilterRequest{region='%s', category='%s', categories=%s, minPrice=%s, maxPrice=%s, name='%s', minStock=%s, maxStock=%s, currency='%s', inStock=%s, page=%d, size=%d, sortBy='%s', sortDirection='%s'}", 
            region, category, categories, minPrice, maxPrice, name, minStock, maxStock, currency, inStock, page, size, sortBy, sortDirection);
    }
}
