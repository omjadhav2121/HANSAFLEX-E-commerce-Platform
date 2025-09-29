package com.hansaflex.ecommerce.repository;

import com.hansaflex.ecommerce.entity.RegionPricingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionPricingConfigRepository extends JpaRepository<RegionPricingConfig, Long> {
    Optional<RegionPricingConfig> findByRegion(String region);
    boolean existsByRegion(String region);
}
