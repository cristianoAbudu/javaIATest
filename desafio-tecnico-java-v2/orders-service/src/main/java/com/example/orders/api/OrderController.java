package com.example.orders.api;

import com.example.orders.api.OrderDtos.CreateOrderRequest;
import com.example.orders.api.OrderDtos.OrderResponse;
import com.example.orders.service.OrderService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                 @Valid @RequestBody CreateOrderRequest request) {
        var created = service.create(request, idempotencyKey);
        return ResponseEntity.created(URI.create("/api/orders/" + created.getId()))
                .body(OrderResponse.from(created));
    }

    @GetMapping
    public Page<OrderResponse> list(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        if (pageable.getPageSize() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O tamanho máximo da página é 100 registros");
        }
        return service.findAll(pageable).map(OrderResponse::from);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable long id) {
        return OrderResponse.from(service.findById(id));
    }
}
