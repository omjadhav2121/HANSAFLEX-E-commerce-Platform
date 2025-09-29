package com.hansaflex.ecommerce.service;

import com.hansaflex.ecommerce.dto.OrderRequest;
import com.hansaflex.ecommerce.dto.OrderResponse;
import com.hansaflex.ecommerce.entity.Order;
import com.hansaflex.ecommerce.entity.Product;
import com.hansaflex.ecommerce.entity.RegionPricingConfig;
import com.hansaflex.ecommerce.enums.OrderStatus;
import com.hansaflex.ecommerce.exception.InsufficientStockException;
import com.hansaflex.ecommerce.exception.ProductNotFoundException;
import com.hansaflex.ecommerce.exception.RegionPricingConfigNotFoundException;
import com.hansaflex.ecommerce.repository.OrderRepository;
import com.hansaflex.ecommerce.repository.ProductRepository;
import com.hansaflex.ecommerce.repository.RegionPricingConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RegionPricingConfigRepository regionPricingConfigRepository;

    @Mock
    private PricingService pricingService;

    @Mock
    private SapIntegrationService sapIntegrationService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private OrderService orderService;

    private Product testProduct;
    private RegionPricingConfig testPricingConfig;
    private OrderRequest testOrderRequest;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .currency("USD")
                .stockQty(10)
                .category("Test")
                .region("US")
                .build();

        testPricingConfig = RegionPricingConfig.builder()
                .id(1L)
                .region("US")
                .vatPercentage(new BigDecimal("8.25"))
                .build();

        OrderRequest.OrderItemRequest itemRequest = OrderRequest.OrderItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();

        testOrderRequest = OrderRequest.builder()
                .items(Arrays.asList(itemRequest))
                .build();
    }

    @Test
    void createOrder_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(regionPricingConfigRepository.findByRegion("US")).thenReturn(Optional.of(testPricingConfig));
        when(pricingService.calculateVatAmount(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("8.25"));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(sapIntegrationService.confirmOrder(any(Long.class), any(BigDecimal.class)))
                .thenReturn("SAP123456");

        // When
        OrderResponse result = orderService.createOrder(testOrderRequest, "customer123", "US");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals("SAP123456", result.getConfirmationNumber());
        assertEquals(1, result.getItems().size());

        verify(productRepository, times(2)).findById(1L);
        verify(regionPricingConfigRepository).findByRegion("US");
        verify(pricingService).calculateVatAmount(new BigDecimal("100.00"), new BigDecimal("8.25"));
        verify(orderRepository, atLeast(2)).save(any(Order.class));
        verify(sapIntegrationService).confirmOrder(eq(1L), any(BigDecimal.class));
        verify(cacheService, times(2)).clearProductCaches();
    }

    @Test
    void createOrder_ProductNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(testOrderRequest, "customer123", "US"));
        verify(productRepository).findById(1L);
        verifyNoInteractions(regionPricingConfigRepository);
        verifyNoInteractions(pricingService);
        verifyNoInteractions(sapIntegrationService);
    }

    @Test
    void createOrder_InsufficientStock() {
        // Given
        testProduct.setStockQty(1); // Less than requested quantity (2)
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(Order.builder()
                .id(1L)
                .customerId("customer123")
                .region("US")
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO)
                .build());

        // When & Then
        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(testOrderRequest, "customer123", "US"));
        verify(productRepository).findById(1L);
        verifyNoInteractions(regionPricingConfigRepository);
        verifyNoInteractions(pricingService);
        verifyNoInteractions(sapIntegrationService);
    }

    @Test
    void createOrder_RegionPricingConfigNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(regionPricingConfigRepository.findByRegion("US")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(Order.builder()
                .id(1L)
                .customerId("customer123")
                .region("US")
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO)
                .build());

        // When & Then
        assertThrows(RegionPricingConfigNotFoundException.class, () -> orderService.createOrder(testOrderRequest, "customer123", "US"));
        verify(productRepository).findById(1L);
        verify(regionPricingConfigRepository).findByRegion("US");
        verifyNoInteractions(pricingService);
        verifyNoInteractions(sapIntegrationService);
    }

    @Test
    void getOrderById_Success() {
        // Given
        Order testOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.CONFIRMED)
                .totalPrice(new BigDecimal("216.50"))
                .confirmationNumber("SAP123456")
                .items(Arrays.asList())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        OrderResponse result = orderService.getOrderById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals("SAP123456", result.getConfirmationNumber());

        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_NotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository).findById(1L);
    }
}
