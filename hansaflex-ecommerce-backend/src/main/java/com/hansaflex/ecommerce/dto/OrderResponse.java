package com.hansaflex.ecommerce.dto;

import com.hansaflex.ecommerce.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private String confirmationNumber;
    private String contactName;
    private String phoneNumber;
    private String deliveryAddress;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String region;
        private BigDecimal vatPercentage;
        private BigDecimal vatAmount;
        private BigDecimal finalPrice;
    }
}
