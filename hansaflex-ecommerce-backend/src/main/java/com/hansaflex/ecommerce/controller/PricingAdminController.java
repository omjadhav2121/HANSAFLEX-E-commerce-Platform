package com.hansaflex.ecommerce.controller;

import com.hansaflex.ecommerce.dto.ApiResponse;
import com.hansaflex.ecommerce.dto.RegionPricingConfigRequest;
import com.hansaflex.ecommerce.dto.RegionPricingConfigResponse;
import com.hansaflex.ecommerce.service.RegionPricingConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PricingAdminController {

    private final RegionPricingConfigService regionPricingConfigService;

    /**
     * Create a new region pricing configuration (Admin only)
     * POST /api/pricing/config
     */
    @PostMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RegionPricingConfigResponse>> createRegionPricingConfig(
            @Valid @RequestBody RegionPricingConfigRequest request) {
        log.info("Admin creating new region pricing config for region: {}", request.getRegion());
        RegionPricingConfigResponse response = regionPricingConfigService.createRegionPricingConfig(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Region pricing config created successfully", response));
    }

    /**
     * Update an existing region pricing configuration (Admin only)
     * PUT /api/pricing/config/{id}
     */
    @PutMapping("/config/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RegionPricingConfigResponse>> updateRegionPricingConfig(
            @PathVariable Long id,
            @Valid @RequestBody RegionPricingConfigRequest request) {
        log.info("Admin updating region pricing config with ID: {}", id);
        RegionPricingConfigResponse response = regionPricingConfigService.updateRegionPricingConfig(id, request);
        return ResponseEntity.ok(ApiResponse.success("Region pricing config updated successfully", response));
    }

    /**
     * Get all region pricing configurations (Admin only)
     * GET /api/pricing/config
     */
    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RegionPricingConfigResponse>>> getAllRegionPricingConfigs() {
        log.info("Admin fetching all region pricing configs");
        List<RegionPricingConfigResponse> configs = regionPricingConfigService.getAllRegionPricingConfigs();
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    /**
     * Get region pricing configuration by ID (Admin only)
     * GET /api/pricing/config/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RegionPricingConfigResponse>> getRegionPricingConfigById(@PathVariable Long id) {
        log.info("Admin fetching region pricing config with ID: {}", id);
        RegionPricingConfigResponse config = regionPricingConfigService.getRegionPricingConfigById(id);
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    /**
     * Delete region pricing configuration by ID (Admin only)
     * DELETE /api/pricing/config/{id}
     */
    @DeleteMapping("/config/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRegionPricingConfig(@PathVariable Long id) {
        log.info("Admin deleting region pricing config with ID: {}", id);
        regionPricingConfigService.deleteRegionPricingConfig(id);
        return ResponseEntity.ok(ApiResponse.success("Region pricing config deleted successfully"));
    }

    /**
     * Get VAT percentage by region (Public endpoint - No authentication required)
     * GET /api/pricing/vat/{region}
     */
    @GetMapping("/vat/{region}")
    public ResponseEntity<ApiResponse<RegionPricingConfigResponse>> getVatByRegion(@PathVariable String region) {
        log.info("Public request to get VAT for region: {}", region);
        RegionPricingConfigResponse config = regionPricingConfigService.getVatByRegion(region);
        return ResponseEntity.ok(ApiResponse.success(config));
    }
}
