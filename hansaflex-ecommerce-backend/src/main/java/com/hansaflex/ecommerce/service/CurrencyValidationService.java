package com.hansaflex.ecommerce.service;

import com.hansaflex.ecommerce.exception.InvalidCurrencyForRegionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class CurrencyValidationService {

    // Region to currency mappings
    private static final Map<String, Set<String>> REGION_CURRENCY_MAPPINGS = new HashMap<>();
    
    static {
        // EU regions can use EUR
        REGION_CURRENCY_MAPPINGS.put("EU", Set.of("EUR"));
        REGION_CURRENCY_MAPPINGS.put("EUROPE", Set.of("EUR"));
        
        // US regions can use USD
        REGION_CURRENCY_MAPPINGS.put("US", Set.of("USD"));
        REGION_CURRENCY_MAPPINGS.put("USA", Set.of("USD"));
        REGION_CURRENCY_MAPPINGS.put("UNITED STATES", Set.of("USD"));
        
        // APAC regions can use SGD, JPY, AUD, etc.
        REGION_CURRENCY_MAPPINGS.put("APAC", Set.of("SGD", "JPY", "AUD", "HKD", "CNY", "KRW", "THB", "MYR", "IDR", "PHP", "VND", "INR"));
        REGION_CURRENCY_MAPPINGS.put("ASIA", Set.of("SGD", "JPY", "AUD", "HKD", "CNY", "KRW", "THB", "MYR", "IDR", "PHP", "VND", "INR"));
        REGION_CURRENCY_MAPPINGS.put("SINGAPORE", Set.of("SGD"));
        REGION_CURRENCY_MAPPINGS.put("JAPAN", Set.of("JPY"));
        REGION_CURRENCY_MAPPINGS.put("AUSTRALIA", Set.of("AUD"));
        REGION_CURRENCY_MAPPINGS.put("HONG KONG", Set.of("HKD"));
        REGION_CURRENCY_MAPPINGS.put("CHINA", Set.of("CNY"));
        REGION_CURRENCY_MAPPINGS.put("SOUTH KOREA", Set.of("KRW"));
        REGION_CURRENCY_MAPPINGS.put("THAILAND", Set.of("THB"));
        REGION_CURRENCY_MAPPINGS.put("MALAYSIA", Set.of("MYR"));
        REGION_CURRENCY_MAPPINGS.put("INDONESIA", Set.of("IDR"));
        REGION_CURRENCY_MAPPINGS.put("PHILIPPINES", Set.of("PHP"));
        REGION_CURRENCY_MAPPINGS.put("VIETNAM", Set.of("VND"));
        REGION_CURRENCY_MAPPINGS.put("INDIA", Set.of("INR"));
        
        // Additional regions
        REGION_CURRENCY_MAPPINGS.put("LATAM", Set.of("USD", "BRL", "MXN", "ARS", "CLP", "COP", "PEN"));
        REGION_CURRENCY_MAPPINGS.put("AFRICA", Set.of("USD", "EUR", "ZAR", "NGN", "EGP", "KES", "GHS"));
        REGION_CURRENCY_MAPPINGS.put("MIDDLE EAST", Set.of("USD", "EUR", "AED", "SAR", "QAR", "KWD", "BHD", "OMR"));
    }

    /**
     * Validates if the given currency is valid for the specified region
     * 
     * @param region The region name
     * @param currency The currency code
     * @throws InvalidCurrencyForRegionException if region is not supported or currency is not valid for the region
     */
    public void validateCurrencyForRegion(String region, String currency) {
        if (region == null || region.trim().isEmpty()) {
            throw new InvalidCurrencyForRegionException("Region cannot be null or empty");
        }
        
        if (currency == null || currency.trim().isEmpty()) {
            throw new InvalidCurrencyForRegionException("Currency cannot be null or empty");
        }
        
        String normalizedRegion = region.trim().toUpperCase();
        String normalizedCurrency = currency.trim().toUpperCase();
        
        log.debug("Validating currency '{}' for region '{}'", normalizedCurrency, normalizedRegion);
        
        Set<String> validCurrencies = REGION_CURRENCY_MAPPINGS.get(normalizedRegion);
        
        if (validCurrencies == null) {
            // Region is not supported - throw exception
            String supportedRegions = String.join(", ", REGION_CURRENCY_MAPPINGS.keySet());
            String errorMessage = String.format("Region '%s' is not supported. Supported regions are: %s", 
                normalizedRegion, supportedRegions);
            log.warn(errorMessage);
            throw new InvalidCurrencyForRegionException(errorMessage);
        }
        
        if (!validCurrencies.contains(normalizedCurrency)) {
            String validCurrenciesList = String.join(", ", validCurrencies);
            String errorMessage = String.format("Currency '%s' is not valid for region '%s'. Valid currencies for this region are: %s", 
                normalizedCurrency, normalizedRegion, validCurrenciesList);
            log.warn(errorMessage);
            throw new InvalidCurrencyForRegionException(errorMessage);
        }
        
        log.debug("Currency '{}' is valid for region '{}'", normalizedCurrency, normalizedRegion);
    }
    
    /**
     * Gets the valid currencies for a given region
     * 
     * @param region The region name
     * @return Set of valid currency codes for the region, or null if region not found
     */
    public Set<String> getValidCurrenciesForRegion(String region) {
        if (region == null || region.trim().isEmpty()) {
            return null;
        }
        
        String normalizedRegion = region.trim().toUpperCase();
        return REGION_CURRENCY_MAPPINGS.get(normalizedRegion);
    }
    
    /**
     * Gets all supported regions
     * 
     * @return Set of all supported region names
     */
    public Set<String> getSupportedRegions() {
        return REGION_CURRENCY_MAPPINGS.keySet();
    }
}
