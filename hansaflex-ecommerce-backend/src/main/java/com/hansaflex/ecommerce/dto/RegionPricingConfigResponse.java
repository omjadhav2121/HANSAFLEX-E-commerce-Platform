package com.hansaflex.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionPricingConfigResponse {
    private Long id;
    private String region;
    private BigDecimal vatPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
