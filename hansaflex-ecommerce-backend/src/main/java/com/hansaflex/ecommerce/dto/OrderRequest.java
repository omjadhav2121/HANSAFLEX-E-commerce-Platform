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
public class OrderRequest {
    
    // For single order: items list
    private List<OrderItemRequest> items;
    
    // For bulk orders: list of orders
    private List<BulkOrderItem> orders;
    
    // Contact and delivery information
    private String contactName;
    private String phoneNumber;
    private String deliveryAddress;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkOrderItem {
        private List<OrderItemRequest> items;
    }
}
