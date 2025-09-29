package com.hansaflex.ecommerce.controller;

import com.hansaflex.ecommerce.dto.ApiResponse;
import com.hansaflex.ecommerce.dto.SapConfirmationRequest;
import com.hansaflex.ecommerce.dto.SapConfirmationResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/mock/sap")
@Slf4j
@CrossOrigin(origins = "*")
public class MockSapController {

    /**
     * Mock SAP confirmation endpoint
     * POST /api/mock/sap/confirm
     */
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<SapConfirmationResponse>> confirmOrder(
            @Valid @RequestBody SapConfirmationRequest request) {
        log.info("Mock SAP confirmation request for order ID: {} with total price: {}", 
                request.getOrderId(), request.getTotalPrice());
        
        // Generate a mock confirmation number
        String confirmationNumber = "SAP" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        
        SapConfirmationResponse response = SapConfirmationResponse.builder()
                .confirmationNumber(confirmationNumber)
                .build();
        
        log.info("Mock SAP confirmation successful: {}", confirmationNumber);
        
        return ResponseEntity.ok(ApiResponse.success("SAP confirmation successful", response));
    }
}
