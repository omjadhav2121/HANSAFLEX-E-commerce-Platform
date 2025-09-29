package com.hansaflex.ecommerce.controller;

import com.hansaflex.ecommerce.dto.ApiResponse;
import com.hansaflex.ecommerce.dto.PriceResponse;
import com.hansaflex.ecommerce.service.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductPriceController {

    private final PricingService pricingService;

    /**
     * Get product price with VAT calculation (Public endpoint - No authentication required)
     * GET /api/products/{id}/price
     */
    @GetMapping("/{id}/price")
    public ResponseEntity<ApiResponse<PriceResponse>> getProductPrice(@PathVariable Long id) {
        log.info("Public request to get price for product ID: {}", id);
        PriceResponse priceResponse = pricingService.calculatePrice(id);
        return ResponseEntity.ok(ApiResponse.success(priceResponse));
    }
}
