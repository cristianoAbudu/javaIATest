package com.example.orders.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.orders.api.OrderDtos.CreateOrderRequest;
import com.example.orders.api.OrderDtos.OrderLineRequest;
import com.example.orders.catalog.CatalogClient;
import com.example.orders.catalog.CatalogProduct;
import com.example.orders.config.DiscountProperties;
import com.example.orders.domain.OrderEntity;
import com.example.orders.domain.OrderStatus;
import com.example.orders.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository repository;
    @Mock CatalogClient catalogClient;
    @InjectMocks OrderService service;

    @Test
    void rejectsMoreThanOneHundredDistinctSkusBeforeCallingCatalog() {
        var properties = new DiscountProperties(new BigDecimal("5"));
        service = new OrderService(repository, catalogClient, properties);
        var lines = java.util.stream.IntStream.range(0, 101)
                .mapToObj(i -> new OrderLineRequest("SKU-" + i, 1)).toList();
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.create(new CreateOrderRequest("Cliente", lines), "idem-1"));
        verifyNoInteractions(catalogClient);
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsMissingIdempotencyKey() {
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.create(new CreateOrderRequest("Cliente",
                        List.of(new OrderLineRequest("SKU-1", 1))), " "));
        verifyNoInteractions(catalogClient, repository);
    }

    @Test
    void createsOrderUsingCatalogPriceAndConfiguredDiscount() {
        var properties = new DiscountProperties(new BigDecimal("10"));
        service = new OrderService(repository, catalogClient, properties);
        when(repository.findDetailedByIdempotencyKey("idem-1")).thenReturn(Optional.empty());
        when(catalogClient.findActiveProducts(List.of("SKU-1")))
                .thenReturn(List.of(new CatalogProduct("SKU-1", "Produto", new BigDecimal("100.00"), true)));
        when(repository.saveAndFlush(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderEntity result = service.create(new CreateOrderRequest("Cliente",
                List.of(new OrderLineRequest("SKU-1", 2))), "idem-1");

        assertEquals(new BigDecimal("200.00"), result.getSubtotal());
        assertEquals(new BigDecimal("20.00"), result.getDiscount());
        assertEquals(new BigDecimal("180.00"), result.getTotal());
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals("idem-1", result.getIdempotencyKey());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void returnsExistingOrderForRepeatedIdempotencyKeyWithoutCallingCatalog() {
        var properties = new DiscountProperties(new BigDecimal("5"));
        service = new OrderService(repository, catalogClient, properties);
        var existing = new OrderEntity("Cliente", "idem-1", new BigDecimal("10.00"),
                new BigDecimal("0.50"), new BigDecimal("9.50"), OrderStatus.CREATED);
        when(repository.findDetailedByIdempotencyKey("idem-1")).thenReturn(Optional.of(existing));

        var result = service.create(new CreateOrderRequest("Outro",
                List.of(new OrderLineRequest("SKU-1", 1))), "idem-1");

        assertSame(existing, result);
        verifyNoInteractions(catalogClient);
        verify(repository, never()).saveAndFlush(any());
    }
}
