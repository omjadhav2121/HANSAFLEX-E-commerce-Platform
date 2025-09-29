package com.hansaflex.ecommerce.service;

import com.hansaflex.ecommerce.dto.PriceResponse;
import com.hansaflex.ecommerce.entity.Product;
import com.hansaflex.ecommerce.entity.RegionPricingConfig;
import com.hansaflex.ecommerce.exception.ProductNotFoundException;
import com.hansaflex.ecommerce.exception.RegionPricingConfigNotFoundException;
import com.hansaflex.ecommerce.repository.ProductRepository;
import com.hansaflex.ecommerce.repository.RegionPricingConfigRepository;
import com.hansaflex.ecommerce.strategy.PricingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PricingService {

    private final ProductRepository productRepository;
    private final RegionPricingConfigRepository regionPricingConfigRepository;
    private final PricingStrategy pricingStrategy;

    public PriceResponse calculatePrice(Long productId) {
        log.info("Calculating price for product ID: {}", productId);
        
        // Fetch product details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        
        // Fetch VAT configuration for the product's region
        RegionPricingConfig pricingConfig = regionPricingConfigRepository.findByRegion(product.getRegion())
                .orElseThrow(() -> new RegionPricingConfigNotFoundException(
                        "Pricing configuration not found for region: " + product.getRegion()));
        
        // Calculate pricing using strategy pattern
        BigDecimal basePrice = product.getPrice();
        BigDecimal vatPercentage = pricingConfig.getVatPercentage();
        BigDecimal finalPrice = pricingStrategy.calculatePrice(basePrice, vatPercentage);
        
        // Calculate VAT amount
        BigDecimal vatAmount = finalPrice.subtract(basePrice);
        
        log.info("Price calculation completed for product {}: basePrice={}, vatPercentage={}, finalPrice={}", 
                product.getName(), basePrice, vatPercentage, finalPrice);
        
        return PriceResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .region(product.getRegion())
                .basePrice(basePrice)
                .vatPercentage(vatPercentage)
                .vatAmount(vatAmount)
                .finalPrice(finalPrice)
                .build();
    }
    
    public BigDecimal calculateVatAmount(BigDecimal basePrice, BigDecimal vatPercentage) {
        return basePrice.multiply(vatPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
