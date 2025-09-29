package com.hansaflex.ecommerce.service;

import com.hansaflex.ecommerce.dto.ApiResponse;
import com.hansaflex.ecommerce.dto.SapConfirmationRequest;
import com.hansaflex.ecommerce.dto.SapConfirmationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class SapIntegrationService {

    private final RestTemplate restTemplate;
    
    @Value("${sap.base-url:http://localhost:8081}")
    private String sapBaseUrl;
    
    public String confirmOrder(Long orderId, java.math.BigDecimal totalPrice) {
        log.info("Confirming order {} with SAP for total price: {}", orderId, totalPrice);
        
        try {
            SapConfirmationRequest request = SapConfirmationRequest.builder()
                    .orderId(orderId)
                    .totalPrice(totalPrice)
                    .build();
            
            String sapUrl = sapBaseUrl + "/api/mock/sap/confirm";
            
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create HTTP entity with request body and headers
            HttpEntity<SapConfirmationRequest> httpEntity = new HttpEntity<>(request, headers);
            
            // Use exchange method with ParameterizedTypeReference to handle generic types
            ParameterizedTypeReference<ApiResponse<SapConfirmationResponse>> responseType = 
                new ParameterizedTypeReference<ApiResponse<SapConfirmationResponse>>() {};
            
            ResponseEntity<ApiResponse<SapConfirmationResponse>> responseEntity = 
                restTemplate.exchange(sapUrl, HttpMethod.POST, httpEntity, responseType);
            
            ApiResponse<SapConfirmationResponse> apiResponse = responseEntity.getBody();
            
            if (apiResponse != null && apiResponse.isSuccess() && apiResponse.getData() != null && 
                apiResponse.getData().getConfirmationNumber() != null) {
                String confirmationNumber = apiResponse.getData().getConfirmationNumber();
                log.info("SAP confirmation successful for order {}: {}", orderId, confirmationNumber);
                return confirmationNumber;
            } else {
                throw new RuntimeException("SAP returned null or empty confirmation number");
            }
            
        } catch (Exception e) {
            log.error("SAP confirmation failed for order {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("SAP confirmation failed: " + e.getMessage(), e);
        }
    }
}
