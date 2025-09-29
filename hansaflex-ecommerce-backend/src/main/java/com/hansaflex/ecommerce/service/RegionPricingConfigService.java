package com.hansaflex.ecommerce.service;

import com.hansaflex.ecommerce.dto.RegionPricingConfigRequest;
import com.hansaflex.ecommerce.dto.RegionPricingConfigResponse;
import com.hansaflex.ecommerce.entity.RegionPricingConfig;
import com.hansaflex.ecommerce.exception.RegionPricingConfigNotFoundException;
import com.hansaflex.ecommerce.repository.RegionPricingConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegionPricingConfigService {

    private final RegionPricingConfigRepository regionPricingConfigRepository;

    public RegionPricingConfigResponse createRegionPricingConfig(RegionPricingConfigRequest request) {
        log.info("Creating region pricing config for region: {}", request.getRegion());
        
        RegionPricingConfig config = RegionPricingConfig.builder()
                .region(request.getRegion())
                .vatPercentage(request.getVatPercentage())
                .build();
        
        RegionPricingConfig savedConfig = regionPricingConfigRepository.save(config);
        log.info("Region pricing config created successfully with ID: {}", savedConfig.getId());
        
        return mapToResponse(savedConfig);
    }

    public RegionPricingConfigResponse updateRegionPricingConfig(Long id, RegionPricingConfigRequest request) {
        log.info("Updating region pricing config with ID: {}", id);
        
        RegionPricingConfig config = regionPricingConfigRepository.findById(id)
                .orElseThrow(() -> new RegionPricingConfigNotFoundException("Region pricing config not found with ID: " + id));
        
        config.setRegion(request.getRegion());
        config.setVatPercentage(request.getVatPercentage());
        
        RegionPricingConfig updatedConfig = regionPricingConfigRepository.save(config);
        log.info("Region pricing config updated successfully");
        
        return mapToResponse(updatedConfig);
    }

    @Transactional(readOnly = true)
    public List<RegionPricingConfigResponse> getAllRegionPricingConfigs() {
        log.info("Fetching all region pricing configs");
        
        return regionPricingConfigRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegionPricingConfigResponse getRegionPricingConfigById(Long id) {
        log.info("Fetching region pricing config with ID: {}", id);
        
        RegionPricingConfig config = regionPricingConfigRepository.findById(id)
                .orElseThrow(() -> new RegionPricingConfigNotFoundException("Region pricing config not found with ID: " + id));
        
        return mapToResponse(config);
    }

    public void deleteRegionPricingConfig(Long id) {
        log.info("Deleting region pricing config with ID: {}", id);
        
        RegionPricingConfig config = regionPricingConfigRepository.findById(id)
                .orElseThrow(() -> new RegionPricingConfigNotFoundException("Region pricing config not found with ID: " + id));
        
        regionPricingConfigRepository.delete(config);
        log.info("Region pricing config deleted successfully");
    }

    @Transactional(readOnly = true)
    public RegionPricingConfigResponse getVatByRegion(String region) {
        log.info("Fetching VAT configuration for region: {}", region);
        
        RegionPricingConfig config = regionPricingConfigRepository.findByRegion(region)
                .orElseThrow(() -> new RegionPricingConfigNotFoundException("VAT configuration not found for region: " + region));
        
        return mapToResponse(config);
    }

    private RegionPricingConfigResponse mapToResponse(RegionPricingConfig config) {
        return RegionPricingConfigResponse.builder()
                .id(config.getId())
                .region(config.getRegion())
                .vatPercentage(config.getVatPercentage())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
    
}
