package com.hansaflex.ecommerce.service;

import com.hansaflex.ecommerce.dto.ProductFilterRequest;
import com.hansaflex.ecommerce.dto.ProductRequest;
import com.hansaflex.ecommerce.dto.ProductResponse;
import com.hansaflex.ecommerce.dto.ProductUpdateRequest;
import com.hansaflex.ecommerce.entity.Product;
import com.hansaflex.ecommerce.exception.ProductNotFoundException;
import com.hansaflex.ecommerce.exception.ProductAlreadyExistsException;
import com.hansaflex.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Create a new product
     */
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating new product: {}", productRequest.getName());
        
        // Currency validation removed - accepting any region and currency combination
        
        // Check if product with same name and region already exists
        if (productRepository.existsByNameAndRegion(productRequest.getName(), productRequest.getRegion())) {
            throw new ProductAlreadyExistsException(
                String.format("Product with name '%s' already exists in region '%s'", 
                    productRequest.getName(), productRequest.getRegion()));
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .currency(productRequest.getCurrency())
                .stockQty(productRequest.getStockQty())
                .category(productRequest.getCategory())
                .region(productRequest.getRegion())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        
        return mapToProductResponse(savedProduct);
    }

    /**
     * Update an existing product
     */
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with ID: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        // Currency validation removed - accepting any region and currency combination

        // Check if another product with same name and region exists (excluding current product)
        if (!existingProduct.getName().equals(productRequest.getName()) || 
            !existingProduct.getRegion().equals(productRequest.getRegion())) {
            
            if (productRepository.existsByNameAndRegion(productRequest.getName(), productRequest.getRegion())) {
                throw new ProductAlreadyExistsException(
                    String.format("Product with name '%s' already exists in region '%s'", 
                        productRequest.getName(), productRequest.getRegion()));
            }
        }

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setCurrency(productRequest.getCurrency());
        existingProduct.setStockQty(productRequest.getStockQty());
        existingProduct.setCategory(productRequest.getCategory());
        existingProduct.setRegion(productRequest.getRegion());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        
        return mapToProductResponse(updatedProduct);
    }

    /**
     * Partially update an existing product
     */
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse partialUpdateProduct(Long id, ProductUpdateRequest productUpdateRequest) {
        log.info("Partially updating product with ID: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        // Check if any fields are provided for update
        boolean hasUpdates = false;

        // Update name if provided
        if (productUpdateRequest.getName() != null && !productUpdateRequest.getName().trim().isEmpty()) {
            // Check if another product with same name and region exists (excluding current product)
            if (!existingProduct.getName().equals(productUpdateRequest.getName()) || 
                (productUpdateRequest.getRegion() != null && !existingProduct.getRegion().equals(productUpdateRequest.getRegion()))) {
                
                String regionToCheck = productUpdateRequest.getRegion() != null ? 
                    productUpdateRequest.getRegion() : existingProduct.getRegion();
                
                if (productRepository.existsByNameAndRegion(productUpdateRequest.getName(), regionToCheck)) {
                    throw new ProductAlreadyExistsException(
                        String.format("Product with name '%s' already exists in region '%s'", 
                            productUpdateRequest.getName(), regionToCheck));
                }
            }
            existingProduct.setName(productUpdateRequest.getName());
            hasUpdates = true;
        }

        // Update description if provided
        if (productUpdateRequest.getDescription() != null && !productUpdateRequest.getDescription().trim().isEmpty()) {
            existingProduct.setDescription(productUpdateRequest.getDescription());
            hasUpdates = true;
        }

        // Update price if provided
        if (productUpdateRequest.getPrice() != null) {
            existingProduct.setPrice(productUpdateRequest.getPrice());
            hasUpdates = true;
        }

        // Update currency if provided
        if (productUpdateRequest.getCurrency() != null && !productUpdateRequest.getCurrency().trim().isEmpty()) {
            // Currency validation removed - accepting any region and currency combination
            
            existingProduct.setCurrency(productUpdateRequest.getCurrency());
            hasUpdates = true;
        }

        // Update stock quantity if provided
        if (productUpdateRequest.getStockQty() != null) {
            existingProduct.setStockQty(productUpdateRequest.getStockQty());
            hasUpdates = true;
        }

        // Update category if provided
        if (productUpdateRequest.getCategory() != null && !productUpdateRequest.getCategory().trim().isEmpty()) {
            existingProduct.setCategory(productUpdateRequest.getCategory());
            hasUpdates = true;
        }

        // Update region if provided
        if (productUpdateRequest.getRegion() != null) {
            // Currency validation removed - accepting any region and currency combination
            
            // Check if another product with same name and new region exists (excluding current product)
            if (!existingProduct.getName().equals(productUpdateRequest.getName() != null ? 
                productUpdateRequest.getName() : existingProduct.getName()) || 
                !existingProduct.getRegion().equals(productUpdateRequest.getRegion())) {
                
                String nameToCheck = productUpdateRequest.getName() != null ? 
                    productUpdateRequest.getName() : existingProduct.getName();
                
                if (productRepository.existsByNameAndRegion(nameToCheck, productUpdateRequest.getRegion())) {
                    throw new ProductAlreadyExistsException(
                        String.format("Product with name '%s' already exists in region '%s'", 
                            nameToCheck, productUpdateRequest.getRegion()));
                }
            }
            existingProduct.setRegion(productUpdateRequest.getRegion());
            hasUpdates = true;
        }

        if (!hasUpdates) {
            throw new IllegalArgumentException("No fields provided for update");
        }

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product partially updated successfully with ID: {}", updatedProduct.getId());
        
        return mapToProductResponse(updatedProduct);
    }

    /**
     * Delete a product
     */
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }
        
        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }

    /**
     * Get product by ID
     */
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        
        return mapToProductResponse(product);
    }

    /**
     * Get all products without any filters (for public API)
     */
    @Cacheable(value = "products", key = "'all_products'")
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products without filters");
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all products with pagination and filtering
     */
    @Cacheable(value = "products", key = "#filterRequest.toString()")
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(ProductFilterRequest filterRequest) {
        log.info("Fetching products with filters: {}", filterRequest);
        
        // Create pageable object
        Sort sort = Sort.by(
            Sort.Direction.fromString(filterRequest.getSortDirection()), 
            filterRequest.getSortBy()
        );
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        
        // Use enhanced filtering if advanced filters are provided
        if (hasAdvancedFilters(filterRequest)) {
            Page<Product> products = productRepository.findProductsWithAdvancedFilters(
                filterRequest.getRegion(),
                filterRequest.getCategory(),
                filterRequest.getCategories(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getName(),
                filterRequest.getMinStock(),
                filterRequest.getMaxStock(),
                filterRequest.getCurrency(),
                filterRequest.getInStock(),
                pageable
            );
            return products.map(this::mapToProductResponse);
        } else {
            // Use basic filtering for backward compatibility
            Page<Product> products = productRepository.findProductsWithFilters(
                filterRequest.getRegion(),
                filterRequest.getCategory(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getName(),
                pageable
            );
            return products.map(this::mapToProductResponse);
        }
    }

    /**
     * Check if advanced filters are being used
     */
    private boolean hasAdvancedFilters(ProductFilterRequest filterRequest) {
        return filterRequest.getCategories() != null && !filterRequest.getCategories().isEmpty() ||
               filterRequest.getMinStock() != null ||
               filterRequest.getMaxStock() != null ||
               filterRequest.getCurrency() != null ||
               filterRequest.getInStock() != null;
    }

    /**
     * Get products by region
     */
    @Cacheable(value = "products", key = "'region_' + #region")
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByRegion(String region) {
        log.info("Fetching products for region: {}", region);
        
        List<Product> products = productRepository.findByRegion(region);
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get products by region with pagination
     */
    @Cacheable(value = "products", key = "'region_' + #region + '_page_' + #page + '_size_' + #size")
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByRegion(String region, int page, int size) {
        log.info("Fetching products for region: {} with pagination (page: {}, size: {})", region, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findByRegion(region, pageable);
        
        return products.map(this::mapToProductResponse);
    }

    /**
     * Get all distinct categories
     */
    @Cacheable(value = "categories")
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        log.info("Fetching all distinct categories");
        return productRepository.findDistinctCategories();
    }

    /**
     * Get all distinct categories by region
     */
    @Cacheable(value = "categories", key = "#region")
    @Transactional(readOnly = true)
    public List<String> getCategoriesByRegion(String region) {
        log.info("Fetching categories for region: {}", region);
        return productRepository.findDistinctCategoriesByRegion(region);
    }

    /**
     * Update stock quantity for a product
     */
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateStock(Long id, Integer newStockQty) {
        log.info("Updating stock for product ID: {} to quantity: {}", id, newStockQty);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        
        product.setStockQty(newStockQty);
        Product updatedProduct = productRepository.save(product);
        
        log.info("Stock updated successfully for product ID: {}", id);
        return mapToProductResponse(updatedProduct);
    }

    /**
     * Get all products in exact column order: id, name, description, price, currency, stockQty, category, region
     */
    @Cacheable(value = "products", key = "'exact_order'")
    @Transactional(readOnly = true)
    public List<Object[]> getAllProductsInExactOrder() {
        log.info("Fetching all products in exact column order");
        return productRepository.findProductsInExactOrder();
    }

    /**
     * Get products by region in exact column order
     */
    @Cacheable(value = "products", key = "'exact_order_region_' + #region")
    @Transactional(readOnly = true)
    public List<Object[]> getProductsByRegionInExactOrder(String region) {
        log.info("Fetching products for region: {} in exact column order", region);
        return productRepository.findProductsByRegionInExactOrder(region);
    }

    /**
     * Get products by category in exact column order
     */
    @Cacheable(value = "products", key = "'exact_order_category_' + #category")
    @Transactional(readOnly = true)
    public List<Object[]> getProductsByCategoryInExactOrder(String category) {
        log.info("Fetching products for category: {} in exact column order", category);
        return productRepository.findProductsByCategoryInExactOrder(category);
    }

    /**
     * Get products with filters in exact column order
     */
    @Cacheable(value = "products", key = "'exact_order_filters_' + #filterRequest.toString()")
    @Transactional(readOnly = true)
    public List<Object[]> getProductsWithFiltersInExactOrder(ProductFilterRequest filterRequest) {
        log.info("Fetching products with filters in exact column order: {}", filterRequest);
        return productRepository.findProductsWithFiltersInExactOrder(
                filterRequest.getRegion(),
                filterRequest.getCategory(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getName()
        );
    }

    // ========== BATCH STOCK UPDATE METHODS ==========

    /**
     * Batch update stock quantities for multiple products
     * Used for bulk order processing with atomic operations
     */
    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public Map<Long, Boolean> batchDeductStock(Map<Long, Integer> productQuantityMap) {
        log.info("Batch deducting stock for {} products", productQuantityMap.size());
        
        Map<Long, Boolean> results = new HashMap<>();
        
        for (Map.Entry<Long, Integer> entry : productQuantityMap.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            
            try {
                int updatedRows = productRepository.deductStock(productId, quantity);
                results.put(productId, updatedRows > 0);
                
                if (updatedRows > 0) {
                    log.info("Successfully deducted {} units from product ID: {}", quantity, productId);
                } else {
                    log.warn("Failed to deduct stock for product ID: {} - insufficient stock or product not found", productId);
                }
            } catch (Exception e) {
                log.error("Error deducting stock for product ID: {}", productId, e);
                results.put(productId, false);
            }
        }
        
        return results;
    }

    /**
     * Check stock availability for multiple products
     */
    @Transactional(readOnly = true)
    public Map<Long, Boolean> checkStockAvailability(Map<Long, Integer> productQuantityMap) {
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

    /**
     * Get products by IDs for batch operations
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByIds(List<Long> productIds) {
        log.info("Fetching {} products by IDs", productIds.size());
        return productRepository.findByIds(productIds);
    }

    /**
     * Check if a product has any orders
     */
    @Transactional(readOnly = true)
    public boolean hasOrders(Long productId) {
        log.info("Checking if product {} has orders", productId);
        return productRepository.hasOrders(productId);
    }

    /**
     * Map Product entity to ProductResponse DTO
     */
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .stockQty(product.getStockQty())
                .category(product.getCategory())
                .region(product.getRegion())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}