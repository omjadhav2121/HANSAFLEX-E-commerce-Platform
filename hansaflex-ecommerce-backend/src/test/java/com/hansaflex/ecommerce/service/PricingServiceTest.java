package com.hansaflex.ecommerce.service;

import com.hansaflex.ecommerce.dto.PriceResponse;
import com.hansaflex.ecommerce.entity.Product;
import com.hansaflex.ecommerce.entity.RegionPricingConfig;
import com.hansaflex.ecommerce.exception.ProductNotFoundException;
import com.hansaflex.ecommerce.exception.RegionPricingConfigNotFoundException;
import com.hansaflex.ecommerce.repository.ProductRepository;
import com.hansaflex.ecommerce.repository.RegionPricingConfigRepository;
import com.hansaflex.ecommerce.strategy.PricingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RegionPricingConfigRepository regionPricingConfigRepository;

    @Mock
    private PricingStrategy pricingStrategy;

    @InjectMocks
    private PricingService pricingService;

    private Product testProduct;
    private RegionPricingConfig testPricingConfig;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .currency("USD")
                .stockQty(10)
                .category("Test")
                .region("US")
                .build();

        testPricingConfig = RegionPricingConfig.builder()
                .id(1L)
                .region("US")
                .vatPercentage(new BigDecimal("8.25"))
                .build();
    }

    @Test
    void calculatePrice_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(regionPricingConfigRepository.findByRegion("US")).thenReturn(Optional.of(testPricingConfig));
        when(pricingStrategy.calculatePrice(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("108.25"));

        // When
        PriceResponse result = pricingService.calculatePrice(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals("Test Product", result.getProductName());
        assertEquals("US", result.getRegion());
        assertEquals(new BigDecimal("100.00"), result.getBasePrice());
        assertEquals(new BigDecimal("8.25"), result.getVatPercentage());
        assertEquals(new BigDecimal("8.25"), result.getVatAmount());
        assertEquals(new BigDecimal("108.25"), result.getFinalPrice());

        verify(productRepository).findById(1L);
        verify(regionPricingConfigRepository).findByRegion("US");
        verify(pricingStrategy).calculatePrice(new BigDecimal("100.00"), new BigDecimal("8.25"));
    }

    @Test
    void calculatePrice_ProductNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> pricingService.calculatePrice(1L));
        verify(productRepository).findById(1L);
        verifyNoInteractions(regionPricingConfigRepository);
        verifyNoInteractions(pricingStrategy);
    }

    @Test
    void calculatePrice_RegionPricingConfigNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(regionPricingConfigRepository.findByRegion("US")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RegionPricingConfigNotFoundException.class, () -> pricingService.calculatePrice(1L));
        verify(productRepository).findById(1L);
        verify(regionPricingConfigRepository).findByRegion("US");
        verifyNoInteractions(pricingStrategy);
    }

    @Test
    void calculateVatAmount_Success() {
        // Given
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal vatPercentage = new BigDecimal("8.25");

        // When
        BigDecimal result = pricingService.calculateVatAmount(basePrice, vatPercentage);

        // Then
        assertEquals(new BigDecimal("8.25"), result);
    }
}
