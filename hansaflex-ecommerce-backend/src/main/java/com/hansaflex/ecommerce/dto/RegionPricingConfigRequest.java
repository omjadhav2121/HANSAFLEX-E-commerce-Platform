package com.hansaflex.ecommerce.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionPricingConfigRequest {
    
    @NotBlank(message = "Region is required")
    @Size(max = 50, message = "Region must not exceed 50 characters")
    private String region;
    
    @NotNull(message = "VAT percentage is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "VAT percentage must not be negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "VAT percentage must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "VAT percentage must have at most 3 integer digits and 2 decimal places")
    private BigDecimal vatPercentage;
}
