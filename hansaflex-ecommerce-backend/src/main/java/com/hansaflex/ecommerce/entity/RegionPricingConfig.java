package com.hansaflex.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "region_pricing_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionPricingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Region is required")
    @Size(max = 50, message = "Region must not exceed 50 characters")
    @Column(nullable = false, unique = true)
    private String region;

    @NotNull(message = "VAT percentage is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "VAT percentage must not be negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "VAT percentage must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "VAT percentage must have at most 3 integer digits and 2 decimal places")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal vatPercentage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
