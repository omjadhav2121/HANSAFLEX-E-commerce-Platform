package com.hansaflex.ecommerce.strategy;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(BigDecimal basePrice, BigDecimal vatPercentage);
}
