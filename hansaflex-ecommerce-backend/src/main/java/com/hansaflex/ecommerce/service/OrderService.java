package com.hansaflex.ecommerce.service;

import com.hansaflex.ecommerce.dto.OrderRequest;
import com.hansaflex.ecommerce.dto.OrderResponse;
import com.hansaflex.ecommerce.dto.OrderResponseWrapper;
import com.hansaflex.ecommerce.entity.Order;
import com.hansaflex.ecommerce.entity.OrderItem;
import com.hansaflex.ecommerce.entity.Product;
import com.hansaflex.ecommerce.entity.RegionPricingConfig;
import com.hansaflex.ecommerce.enums.OrderStatus;
import com.hansaflex.ecommerce.exception.InsufficientStockException;
import com.hansaflex.ecommerce.exception.ProductNotFoundException;
import com.hansaflex.ecommerce.exception.ProductRegionMismatchException;
import com.hansaflex.ecommerce.exception.RegionPricingConfigNotFoundException;
import com.hansaflex.ecommerce.repository.OrderRepository;
import com.hansaflex.ecommerce.repository.ProductRepository;
import com.hansaflex.ecommerce.repository.RegionPricingConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RegionPricingConfigRepository regionPricingConfigRepository;
    private final PricingService pricingService;
    private final SapIntegrationService sapIntegrationService;
    private final CacheService cacheService;
    @CacheEvict(value = {"products", "productDetails", "productPrice"}, allEntries = true)
    public OrderResponse createOrder(OrderRequest orderRequest, String customerId, String region) {
        log.info("Creating order with {} items for customer {} in region {}", 
                orderRequest.getItems().size(), customerId, region);
        
        try {
            // Create order with CREATED status initially
            Order order = Order.builder()
                    .customerId(customerId)
                    .region(region)
                    .status(OrderStatus.CREATED)
                    .totalPrice(BigDecimal.ZERO)
                    .contactName(orderRequest.getContactName())
                    .phoneNumber(orderRequest.getPhoneNumber())
                    .deliveryAddress(orderRequest.getDeliveryAddress())
                    .items(new ArrayList<>())
                    .build();
            
            order = orderRepository.save(order);
            
            BigDecimal totalPrice = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();
            
            // Process each order item
            for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
                OrderItem orderItem = processOrderItem(order, itemRequest);
                orderItems.add(orderItem);
                totalPrice = totalPrice.add(orderItem.getFinalPrice());
            }
            
            // Update order with calculated total
            order.setItems(orderItems);
            order.setTotalPrice(totalPrice);
            order = orderRepository.save(order);
            
            // Update stock quantities separately to ensure they're committed
            for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
                updateProductStock(itemRequest.getProductId(), itemRequest.getQuantity());
            }
            
            // Call SAP integration service
            String confirmationNumber = sapIntegrationService.confirmOrder(order.getId(), order.getTotalPrice());
            order.setConfirmationNumber(confirmationNumber);
            order.setStatus(OrderStatus.CONFIRMED);
            order = orderRepository.save(order);
            
            log.info("Order created successfully with ID: {} and confirmation number: {}", 
                    order.getId(), order.getConfirmationNumber());
            
            // Explicitly clear product caches to ensure immediate UI updates
            cacheService.clearProductCaches();
            log.info("Explicitly cleared product caches after order creation");
            
            return mapToOrderResponse(order);
            
        } catch (ProductRegionMismatchException | InsufficientStockException | ProductNotFoundException | RegionPricingConfigNotFoundException e) {
            // Re-throw specific exceptions as-is so they can be handled by the global exception handler
            throw e;
        } catch (Exception e) {
            log.error("Failed to create order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create order(s) - supports both single and bulk orders
     * @param orderRequest Can contain either 'items' for single order or 'orders' for bulk orders
     * @param customerId Customer ID from authentication
     * @param region Customer region from authentication
     * @return OrderResponseWrapper containing either single order or bulk order results
     */
    @CacheEvict(value = {"products", "productDetails", "productPrice"}, allEntries = true)
    public OrderResponseWrapper createOrderFlexible(OrderRequest orderRequest, String customerId, String region) {
        log.info("Creating order(s) for customer {} in region {}", customerId, region);
        
        // Check if this is a bulk order request
        if (orderRequest.getOrders() != null && !orderRequest.getOrders().isEmpty()) {
            return createBulkOrders(orderRequest.getOrders(), customerId, region);
        } 
        // Single order request
        else if (orderRequest.getItems() != null && !orderRequest.getItems().isEmpty()) {
            OrderResponse singleOrder = createOrder(orderRequest, customerId, region);
            return OrderResponseWrapper.builder()
                    .order(singleOrder)
                    .build();
        } 
        else {
            throw new IllegalArgumentException("Order request must contain either 'items' for single order or 'orders' for bulk orders");
        }
    }
    
    /**
     * Create multiple orders in a single transaction with batch stock deduction
     */
    @Transactional
    @CacheEvict(value = {"products", "productDetails", "productPrice"}, allEntries = true)
    public OrderResponseWrapper createBulkOrders(List<OrderRequest.BulkOrderItem> bulkOrders, String customerId, String region) {
        log.info("Creating {} bulk orders for customer {} in region {}", bulkOrders.size(), customerId, region);
        
        List<OrderResponse> successfulOrders = new ArrayList<>();
        List<OrderResponseWrapper.OrderResult> results = new ArrayList<>();
        int successfulCount = 0;
        int failedCount = 0;
        
        // First, collect all product-quantity mappings for batch stock check
        Map<Long, Integer> allProductQuantities = new HashMap<>();
        for (int i = 0; i < bulkOrders.size(); i++) {
            OrderRequest.BulkOrderItem bulkOrder = bulkOrders.get(i);
            for (OrderRequest.OrderItemRequest item : bulkOrder.getItems()) {
                allProductQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
            }
        }
        
        // Check stock availability for all products at once
        Map<Long, Boolean> stockAvailability = checkStockAvailability(allProductQuantities);
        
        // Process each order
        for (int i = 0; i < bulkOrders.size(); i++) {
            OrderRequest.BulkOrderItem bulkOrder = bulkOrders.get(i);
            OrderResponseWrapper.OrderResult result = new OrderResponseWrapper.OrderResult();
            result.setOrderIndex(i);
            
            try {
                // Check if all products in this order have sufficient stock
                boolean allProductsAvailable = true;
                for (OrderRequest.OrderItemRequest item : bulkOrder.getItems()) {
                    if (!stockAvailability.getOrDefault(item.getProductId(), false)) {
                        allProductsAvailable = false;
                        break;
                    }
                }
                
                if (!allProductsAvailable) {
                    result.setSuccess(false);
                    result.setMessage("Insufficient stock for one or more products in this order");
                    result.setError("STOCK_UNAVAILABLE");
                    failedCount++;
                } else {
                    // Convert to regular OrderRequest and process
                    OrderRequest regularOrderRequest = OrderRequest.builder()
                            .items(bulkOrder.getItems())
                            .build();
                    OrderResponse orderResponse = createOrder(regularOrderRequest, customerId, region);
                    
                    result.setSuccess(true);
                    result.setMessage("Order processed successfully");
                    result.setOrderResponse(orderResponse);
                    successfulOrders.add(orderResponse);
                    successfulCount++;
                }
                
            } catch (Exception e) {
                log.error("Error processing order {}: {}", i, e.getMessage(), e);
                result.setSuccess(false);
                result.setMessage("Error processing order: " + e.getMessage());
                result.setError(e.getClass().getSimpleName());
                failedCount++;
            }
            
            results.add(result);
        }
        
        return OrderResponseWrapper.builder()
                .orders(successfulOrders)
                .totalOrders(bulkOrders.size())
                .successfulOrders(successfulCount)
                .failedOrders(failedCount)
                .results(results)
                .build();
    }
    
    private OrderItem processOrderItem(Order order, OrderRequest.OrderItemRequest itemRequest) {
        // Fetch product details
        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + itemRequest.getProductId()));
        
        // Validate that product's region matches customer's region
        if (!product.getRegion().equalsIgnoreCase(order.getRegion())) {
            throw new ProductRegionMismatchException(
                    String.format("Product %s is not available in region %s", 
                            product.getName(), order.getRegion()));
        }
        
        // Check stock availability
        if (product.getStockQty() < itemRequest.getQuantity()) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock for product %s. Available: %d, Requested: %d", 
                            product.getName(), product.getStockQty(), itemRequest.getQuantity()));
        }
        
        // Fetch VAT configuration using order's region
        RegionPricingConfig pricingConfig = regionPricingConfigRepository.findByRegion(order.getRegion())
                .orElseThrow(() -> new RegionPricingConfigNotFoundException(
                        "Pricing configuration not found for region: " + order.getRegion()));
        
        // Calculate pricing
        BigDecimal unitPrice = product.getPrice();
        BigDecimal vatPercentage = pricingConfig.getVatPercentage();
        BigDecimal vatAmount = pricingService.calculateVatAmount(unitPrice, vatPercentage);
        BigDecimal finalUnitPrice = unitPrice.add(vatAmount);
        BigDecimal finalPrice = finalUnitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
        
        // Create order item
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(itemRequest.getQuantity())
                .unitPrice(unitPrice)
                .region(order.getRegion())
                .vatPercentage(vatPercentage)
                .vatAmount(vatAmount)
                .finalPrice(finalPrice)
                .build();
        
        return orderItem;
    }
    
    @CacheEvict(value = {"products", "productDetails", "productPrice"}, allEntries = true)
    @Transactional
    public void updateProductStock(Long productId, Integer quantity) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
            
            int oldStock = product.getStockQty();
            int newStock = oldStock - quantity;
            if (newStock < 0) {
                throw new InsufficientStockException("Insufficient stock for product " + product.getName() + 
                        ". Available: " + oldStock + ", Requested: " + quantity);
            }
            
            product.setStockQty(newStock);
            productRepository.save(product);
            log.info("Updated stock for product {} (ID: {}) from {} to {} - Cache will be evicted", product.getName(), productId, oldStock, newStock);
            
            // Explicitly clear product caches to ensure immediate UI updates
            cacheService.clearProductCaches();
            log.info("Explicitly cleared product caches after stock update for product {}", productId);
        } catch (Exception e) {
            log.error("Failed to update stock for product {}: {}", productId, e.getMessage(), e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        return mapToOrderResponse(order);
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByRegion(String region) {
        log.info("Fetching orders for region: {}", region);
        
        List<Order> orders = orderRepository.findByRegion(region);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(String customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }
    

    /**
     * Check stock availability for multiple products
     */
    private Map<Long, Boolean> checkStockAvailability(Map<Long, Integer> productQuantityMap) {
        log.info("Checking stock availability for {} products", productQuantityMap.size());
        
        Map<Long, Boolean> results = new HashMap<>();
        
        for (Map.Entry<Long, Integer> entry : productQuantityMap.entrySet()) {
            Long productId = entry.getKey();
            Integer requiredQuantity = entry.getValue();
            
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                boolean available = product.getStockQty() >= requiredQuantity;
                results.put(productId, available);
                
                if (!available) {
                    log.warn("Insufficient stock for product ID: {} - Available: {}, Required: {}", 
                            productId, product.getStockQty(), requiredQuantity);
                }
            } else {
                log.warn("Product not found with ID: {}", productId);
                results.put(productId, false);
            }
        }
        
        return results;
    }


    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .region(item.getRegion())
                        .vatPercentage(item.getVatPercentage())
                        .vatAmount(item.getVatAmount())
                        .finalPrice(item.getFinalPrice())
                        .build())
                .toList();
        
        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .confirmationNumber(order.getConfirmationNumber())
                .contactName(order.getContactName())
                .phoneNumber(order.getPhoneNumber())
                .deliveryAddress(order.getDeliveryAddress())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}
