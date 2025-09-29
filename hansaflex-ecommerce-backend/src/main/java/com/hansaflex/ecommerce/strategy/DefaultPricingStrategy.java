package com.hansaflex.ecommerce.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DefaultPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice, BigDecimal vatPercentage) {
        if (basePrice == null || vatPercentage == null) {
            throw new IllegalArgumentException("Base price and VAT percentage cannot be null");
        }
        
        // Calculate VAT amount: basePrice * (vatPercentage / 100)
        BigDecimal vatAmount = basePrice.multiply(vatPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        
        // Calculate final price: basePrice + vatAmount
        BigDecimal finalPrice = basePrice.add(vatAmount);
        
        // Round to 2 decimal places
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
}
