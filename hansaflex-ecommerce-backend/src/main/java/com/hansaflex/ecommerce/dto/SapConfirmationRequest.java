package com.hansaflex.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SapConfirmationRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "Total price is required")
    private BigDecimal totalPrice;
}
