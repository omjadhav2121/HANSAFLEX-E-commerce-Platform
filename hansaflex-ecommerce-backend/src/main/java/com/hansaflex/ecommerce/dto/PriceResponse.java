package com.hansaflex.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceResponse {
    private Long productId;
    private String productName;
    private String region;
    private BigDecimal basePrice;
    private BigDecimal vatPercentage;
    private BigDecimal vatAmount;
    private BigDecimal finalPrice;
}
