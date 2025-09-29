package com.hansaflex.ecommerce.repository;

import com.hansaflex.ecommerce.entity.Order;
import com.hansaflex.ecommerce.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findById(Long id);
    
    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.region = :region")
    List<Order> findByRegion(@Param("region") String region);
    
    List<Order> findByCustomerId(String customerId);
    
    List<Order> findByStatus(OrderStatus status);
}
