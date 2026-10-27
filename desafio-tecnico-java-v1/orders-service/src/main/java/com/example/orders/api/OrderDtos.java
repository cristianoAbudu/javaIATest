package com.example.orders.api;

import com.example.orders.domain.OrderEntity;
import com.example.orders.domain.OrderItemEntity;
import com.example.orders.domain.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {
    private OrderDtos() {}

    public record CreateOrderRequest(
            @NotBlank String customerName,
            @NotEmpty List<@Valid OrderLineRequest> items) {}

    public record OrderLineRequest(@NotBlank String sku, @Positive int quantity) {}

    public record OrderItemResponse(String sku, String productName, int quantity,
                                    BigDecimal unitPrice, BigDecimal lineTotal) {}

    public record OrderResponse(Long id, String customerName, List<OrderItemResponse> items,
                                BigDecimal subtotal, BigDecimal discount, BigDecimal total,
                                OrderStatus status, Instant createdAt) {
        public static OrderResponse from(OrderEntity order) {
            return new OrderResponse(order.getId(), order.getCustomerName(),
                    order.getItems().stream().map(OrderDtos::from).toList(),
                    order.getSubtotal(), order.getDiscount(), order.getTotal(),
                    order.getStatus(), order.getCreatedAt());
        }
    }

    private static OrderItemResponse from(OrderItemEntity item) {
        return new OrderItemResponse(item.getSku(), item.getProductName(), item.getQuantity(),
                item.getUnitPrice(), item.getLineTotal());
    }
}
