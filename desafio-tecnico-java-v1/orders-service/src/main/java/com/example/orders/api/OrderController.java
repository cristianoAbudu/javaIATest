package com.example.orders.api;

import com.example.orders.api.OrderDtos.CreateOrderRequest;
import com.example.orders.api.OrderDtos.OrderResponse;
import com.example.orders.service.OrderService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        var created = service.create(request);
        return ResponseEntity.created(URI.create("/api/orders/" + created.getId()))
                .body(OrderResponse.from(created));
    }

    @GetMapping
    public List<OrderResponse> list() {
        return service.findAll().stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable long id) {
        return OrderResponse.from(service.findById(id));
    }
}
