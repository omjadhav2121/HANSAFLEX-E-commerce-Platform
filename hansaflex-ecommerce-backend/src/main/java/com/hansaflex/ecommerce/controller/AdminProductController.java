package com.hansaflex.ecommerce.controller;

import com.hansaflex.ecommerce.dto.ApiResponse;
import com.hansaflex.ecommerce.dto.ProductRequest;
import com.hansaflex.ecommerce.dto.ProductResponse;
import com.hansaflex.ecommerce.dto.ProductUpdateRequest;
import com.hansaflex.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminProductController {

    private final ProductService productService;

    /**
     * Create a new product (Admin only)
     * POST /api/admin/products
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        log.info("Admin creating new product: {}", productRequest.getName());
        ProductResponse productResponse = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", productResponse));
    }

    /**
     * Update an existing product (Admin only)
     * PUT /api/admin/products/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductRequest productRequest) {
        log.info("Admin updating product with ID: {}", id);
        ProductResponse productResponse = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", productResponse));
    }

    /**
     * Partially update an existing product (Admin only)
     * PATCH /api/admin/products/{id}
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> partialUpdateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductUpdateRequest productUpdateRequest) {
        log.info("Admin partially updating product with ID: {}", id);
        ProductResponse productResponse = productService.partialUpdateProduct(id, productUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", productResponse));
    }

    /**
     * Delete a product (Admin only)
     * DELETE /api/admin/products/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        log.info("Admin deleting product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    /**
     * Check if a product has orders (Admin only)
     * GET /api/admin/products/{id}/has-orders
     */
    @GetMapping("/{id}/has-orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> checkProductHasOrders(@PathVariable Long id) {
        log.info("Admin checking if product {} has orders", id);
        boolean hasOrders = productService.hasOrders(id);
        return ResponseEntity.ok(ApiResponse.success("Product order check completed", hasOrders));
    }
}
