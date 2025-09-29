package com.hansaflex.ecommerce.controller;

import com.hansaflex.ecommerce.dto.ApiResponse;
import com.hansaflex.ecommerce.dto.OrderRequest;
import com.hansaflex.ecommerce.dto.OrderResponse;
import com.hansaflex.ecommerce.dto.OrderResponseWrapper;
import com.hansaflex.ecommerce.security.JwtUtil;
import com.hansaflex.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    /**
     * Place a new order or bulk orders (Customer authentication required)
     * POST /api/orders
     * 
     * Supports both single and bulk orders:
     * - Single order: {"items": [{"productId": 1, "quantity": 2}]}
     * - Bulk orders: {"orders": [{"items": [{"productId": 1, "quantity": 2}]}, {"items": [{"productId": 3, "quantity": 1}]}]}
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseWrapper>> createOrder(
            @RequestBody OrderRequest orderRequest,
            HttpServletRequest request) {
        // Determine if this is a single or bulk order request
        boolean isBulkOrder = orderRequest.getOrders() != null && !orderRequest.getOrders().isEmpty();
        boolean isSingleOrder = orderRequest.getItems() != null && !orderRequest.getItems().isEmpty();
        
        if (!isBulkOrder && !isSingleOrder) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Order request must contain either 'items' for single order or 'orders' for bulk orders"));
        }
        
        // Validate bulk order items
        if (isBulkOrder) {
            for (OrderRequest.BulkOrderItem bulkOrder : orderRequest.getOrders()) {
                if (bulkOrder.getItems() == null || bulkOrder.getItems().isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Each order in bulk request must contain items"));
                }
                // Validate individual items
                for (OrderRequest.OrderItemRequest item : bulkOrder.getItems()) {
                    if (item.getProductId() == null || item.getQuantity() == null) {
                        return ResponseEntity.badRequest()
                                .body(ApiResponse.error("Product ID and quantity are required for each item"));
                    }
                }
            }
        }
        
        // Validate single order items
        if (isSingleOrder) {
            for (OrderRequest.OrderItemRequest item : orderRequest.getItems()) {
                if (item.getProductId() == null || item.getQuantity() == null) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Product ID and quantity are required for each item"));
                }
            }
        }
        
        log.info("Creating {} order(s)", isBulkOrder ? "bulk" : "single");
        
        // Extract JWT token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }
        
        String customerId;
        String region;
        
        if (authHeader.startsWith("Bearer ")) {
            // JWT Authentication
            String token = authHeader.substring(7);
            customerId = jwtUtil.extractCustomerId(token);
            region = jwtUtil.extractRegion(token);
            
            if (customerId == null || region == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid JWT token: missing customer information"));
            }
        } else if (authHeader.startsWith("Basic ")) {
            // Basic Authentication - extract user info from SecurityContext
            try {
                org.springframework.security.core.Authentication auth = 
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                
                if (auth == null || !auth.isAuthenticated()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ApiResponse.error("Authentication required"));
                }
                
                com.hansaflex.ecommerce.entity.User user = (com.hansaflex.ecommerce.entity.User) auth.getPrincipal();
                customerId = user.getId().toString();
                region = user.getRegion();
                
                if (region == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ApiResponse.error("User region not found. Please ensure user has a valid region."));
                }
            } catch (Exception e) {
                log.error("Error extracting user info from Basic Auth: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid authentication"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unsupported authentication method. Use Bearer (JWT) or Basic authentication."));
        }
        
        try {
            OrderResponseWrapper orderResponse = orderService.createOrderFlexible(orderRequest, customerId, region);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Order(s) created successfully", orderResponse));
        } catch (Exception e) {
            log.error("Failed to create order(s): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create order(s): " + e.getMessage()));
        }
    }

    /**
     * Get order details by ID (Public endpoint - No authentication required)
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        log.info("Fetching order with ID: {}", id);
        OrderResponse orderResponse = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(orderResponse));
    }

    /**
     * Get all orders (Admin only - JWT or Basic Auth)
     * GET /api/orders
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        log.info("Admin fetching all orders");
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * Get orders by region (Admin only - JWT or Basic Auth)
     * GET /api/orders/region/{region}
     */
    @GetMapping("/region/{region}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByRegion(@PathVariable String region) {
        log.info("Admin fetching orders for region: {}", region);
        List<OrderResponse> orders = orderService.getOrdersByRegion(region);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * Get customer's own orders (Customer authentication required)
     * GET /api/orders/my
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(HttpServletRequest request) {
        log.info("Customer fetching their own orders");
        
        // Extract JWT token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }
        
        String customerId;
        
        if (authHeader.startsWith("Bearer ")) {
            // JWT Authentication
            String token = authHeader.substring(7);
            customerId = jwtUtil.extractCustomerId(token);
            
            if (customerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid JWT token: missing customer information"));
            }
        } else if (authHeader.startsWith("Basic ")) {
            // Basic Authentication - extract user info from SecurityContext
            try {
                org.springframework.security.core.Authentication auth = 
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                
                if (auth == null || !auth.isAuthenticated()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ApiResponse.error("Authentication required"));
                }
                
                com.hansaflex.ecommerce.entity.User user = (com.hansaflex.ecommerce.entity.User) auth.getPrincipal();
                customerId = user.getId().toString();
            } catch (Exception e) {
                log.error("Error extracting user info from Basic Auth: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid authentication"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unsupported authentication method. Use Bearer (JWT) or Basic authentication."));
        }
        
        List<OrderResponse> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }


}
