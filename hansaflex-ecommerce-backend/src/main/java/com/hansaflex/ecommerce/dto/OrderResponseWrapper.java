package com.hansaflex.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseWrapper {
    
    // For single order response
    private OrderResponse order;
    
    // For bulk order response
    private List<OrderResponse> orders;
    private int totalOrders;
    private int successfulOrders;
    private int failedOrders;
    private List<OrderResult> results;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResult {
        private int orderIndex;
        private boolean success;
        private String message;
        private String error;
        private OrderResponse orderResponse;
    }
}
