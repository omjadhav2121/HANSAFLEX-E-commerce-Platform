package com.hansaflex.ecommerce.controller;

import com.hansaflex.ecommerce.dto.ApiResponse;
import com.hansaflex.ecommerce.dto.ProductFilterRequest;
import com.hansaflex.ecommerce.dto.ProductResponse;
import com.hansaflex.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CatalogController {

    private final ProductService productService;

    /**
     * Get all products (Public endpoint - No authentication required)
     * GET /api/catalog/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        log.info("Public request to fetch all products");
        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .page(0)
                .size(1000) // Large size to get all products
                .sortBy("name")
                .sortDirection("asc")
                .build();
        Page<ProductResponse> productsPage = productService.getAllProducts(filterRequest);
        List<ProductResponse> products = productsPage.getContent();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * Single combined API for pagination and filtering (Public endpoint - No authentication required)
     * GET /api/catalog?region=EU&category=Hydraulics&minPrice=100&maxPrice=500&page=0&size=10&sortBy=name&sortDirection=asc
     * Enhanced with advanced filtering options:
     * - categories: Multiple categories (comma-separated)
     * - minStock, maxStock: Stock quantity range
     * - currency: Filter by currency
     * - inStock: Only products with stock > 0
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsWithPaginationAndFiltering(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String categories, // Comma-separated list
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        log.info("Customer fetching products with enhanced pagination and filtering - region: {}, category: {}, categories: {}, minPrice: {}, maxPrice: {}, name: {}, minStock: {}, maxStock: {}, currency: {}, inStock: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                region, category, categories, minPrice, maxPrice, name, minStock, maxStock, currency, inStock, page, size, sortBy, sortDirection);

        // Parse categories if provided
        List<String> categoryList = null;
        if (categories != null && !categories.trim().isEmpty()) {
            categoryList = List.of(categories.split(","));
        }

        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .region(region)
                .category(category)
                .categories(categoryList)
                .minPrice(minPrice != null ? java.math.BigDecimal.valueOf(Double.parseDouble(minPrice)) : null)
                .maxPrice(maxPrice != null ? java.math.BigDecimal.valueOf(Double.parseDouble(maxPrice)) : null)
                .name(name)
                .minStock(minStock)
                .maxStock(maxStock)
                .currency(currency)
                .inStock(inStock)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<ProductResponse> products = productService.getAllProducts(filterRequest);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}
