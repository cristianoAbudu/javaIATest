package com.example.orders.repository;

import com.example.orders.domain.OrderEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByIdempotencyKey(String idempotencyKey);

    @EntityGraph(attributePaths = "items")
    @Query("select o from OrderEntity o where o.idempotencyKey = :idempotencyKey")
    Optional<OrderEntity> findDetailedByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);

    @EntityGraph(attributePaths = "items")
    @Query(value = "select o from OrderEntity o order by o.createdAt desc",
           countQuery = "select count(o) from OrderEntity o")
    Page<OrderEntity> findAllWithItems(Pageable pageable);

    @EntityGraph(attributePaths = "items")
    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findDetailedById(@Param("id") long id);
}
