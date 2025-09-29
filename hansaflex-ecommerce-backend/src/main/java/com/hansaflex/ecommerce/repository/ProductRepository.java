package com.hansaflex.ecommerce.repository;

import com.hansaflex.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products by region
     */
    List<Product> findByRegion(String region);

    /**
     * Find all products by region with pagination
     */
    Page<Product> findByRegion(String region, Pageable pageable);

    /**
     * Find products by category
     */
    List<Product> findByCategory(String category);

    /**
     * Find products by category with pagination
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * Find products by region and category
     */
    List<Product> findByRegionAndCategory(String region, String category);

    /**
     * Find products by region and category with pagination
     */
    Page<Product> findByRegionAndCategory(String region, String category, Pageable pageable);

    /**
     * Find products by price range
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products by price range with pagination
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Find products by name containing (case insensitive)
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find products by name containing with pagination
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find products with stock quantity greater than specified value
     */
    List<Product> findByStockQtyGreaterThan(Integer stockQty);

    /**
     * Find products with stock quantity greater than specified value with pagination
     */
    Page<Product> findByStockQtyGreaterThan(Integer stockQty, Pageable pageable);

    /**
     * Custom query for complex filtering with pagination
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:region IS NULL OR p.region = :region) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> findProductsWithFilters(
            @Param("region") String region,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("name") String name,
            Pageable pageable);

    /**
     * Enhanced custom query for advanced filtering with pagination
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:region IS NULL OR p.region = :region) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:categories IS NULL OR p.category IN :categories) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:minStock IS NULL OR p.stockQty >= :minStock) AND " +
           "(:maxStock IS NULL OR p.stockQty <= :maxStock) AND " +
           "(:currency IS NULL OR p.currency = :currency) AND " +
           "(:inStock IS NULL OR (:inStock = true AND p.stockQty > 0) OR (:inStock = false AND p.stockQty = 0))")
    Page<Product> findProductsWithAdvancedFilters(
            @Param("region") String region,
            @Param("category") String category,
            @Param("categories") List<String> categories,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("name") String name,
            @Param("minStock") Integer minStock,
            @Param("maxStock") Integer maxStock,
            @Param("currency") String currency,
            @Param("inStock") Boolean inStock,
            Pageable pageable);

    /**
     * Find all distinct categories
     */
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findDistinctCategories();

    /**
     * Find all distinct categories by region
     */
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.region = :region ORDER BY p.category")
    List<String> findDistinctCategoriesByRegion(@Param("region") String region);

    /**
     * Check if product exists by name and region
     */
    boolean existsByNameAndRegion(String name, String region);

    /**
     * Find product by name and region
     */
    Optional<Product> findByNameAndRegion(String name, String region);

    /**
     * Custom query to get products in exact column order: id, name, description, price, currency, stockQty, category, region
     * This ensures the columns are returned in the specific order requested
     */
    @Query("SELECT p.id, p.name, p.description, p.price, p.currency, p.stockQty, p.category, p.region " +
           "FROM Product p ORDER BY p.id")
    List<Object[]> findProductsInExactOrder();

    /**
     * Custom query to get products in exact column order with pagination
     */
    @Query("SELECT p.id, p.name, p.description, p.price, p.currency, p.stockQty, p.category, p.region " +
           "FROM Product p ORDER BY p.id")
    Page<Object[]> findProductsInExactOrder(Pageable pageable);

    /**
     * Custom query to get products by region in exact column order
     */
    @Query("SELECT p.id, p.name, p.description, p.price, p.currency, p.stockQty, p.category, p.region " +
           "FROM Product p WHERE p.region = :region ORDER BY p.id")
    List<Object[]> findProductsByRegionInExactOrder(@Param("region") String region);

    /**
     * Custom query to get products by category in exact column order
     */
    @Query("SELECT p.id, p.name, p.description, p.price, p.currency, p.stockQty, p.category, p.region " +
           "FROM Product p WHERE p.category = :category ORDER BY p.id")
    List<Object[]> findProductsByCategoryInExactOrder(@Param("category") String category);

    /**
     * Custom query to get products with filters in exact column order
     */
    @Query("SELECT p.id, p.name, p.description, p.price, p.currency, p.stockQty, p.category, p.region " +
           "FROM Product p WHERE " +
           "(:region IS NULL OR p.region = :region) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "ORDER BY p.id")
    List<Object[]> findProductsWithFiltersInExactOrder(
            @Param("region") String region,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("name") String name);

    /**
     * Custom query to get products with filters in exact column order with pagination
     */
    @Query("SELECT p.id, p.name, p.description, p.price, p.currency, p.stockQty, p.category, p.region " +
           "FROM Product p WHERE " +
           "(:region IS NULL OR p.region = :region) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "ORDER BY p.id")
    Page<Object[]> findProductsWithFiltersInExactOrder(
            @Param("region") String region,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("name") String name,
            Pageable pageable);

    // ========== BATCH UPDATE METHODS FOR STOCK DEDUCTION ==========

    /**
     * Batch update stock quantity for a single product
     * Used for atomic stock deduction during order processing
     */
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQty = p.stockQty - :quantity WHERE p.id = :productId AND p.stockQty >= :quantity")
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * Batch update stock quantities for multiple products
     * Used for bulk order processing
     */
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQty = p.stockQty - :quantity WHERE p.id = :productId AND p.stockQty >= :quantity")
    int batchDeductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * Find products by IDs for batch operations
     */
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> findByIds(@Param("productIds") List<Long> productIds);

    /**
     * Check stock availability for multiple products
     */
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds AND p.stockQty >= :quantities")
    List<Product> checkStockAvailability(@Param("productIds") List<Long> productIds, @Param("quantities") List<Integer> quantities);

    /**
     * Check if a product has any order items (orders)
     */
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.product.id = :productId")
    boolean hasOrders(@Param("productId") Long productId);
}