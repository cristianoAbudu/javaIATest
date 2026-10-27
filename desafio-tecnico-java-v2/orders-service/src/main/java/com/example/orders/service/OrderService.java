package com.example.orders.service;

import com.example.orders.api.OrderDtos.CreateOrderRequest;
import com.example.orders.catalog.CatalogClient;
import com.example.orders.catalog.CatalogProduct;
import com.example.orders.config.DiscountProperties;
import com.example.orders.domain.*;
import com.example.orders.repository.OrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final CatalogClient catalogClient;
    private final DiscountProperties discountProperties;

    public OrderService(OrderRepository repository, CatalogClient catalogClient,
                        DiscountProperties discountProperties) {
        this.repository = repository;
        this.catalogClient = catalogClient;
        this.discountProperties = discountProperties;
    }

    @Transactional
    public OrderEntity create(CreateOrderRequest request, String idempotencyKey) {
        String normalizedKey = normalizeIdempotencyKey(idempotencyKey);
        Optional<OrderEntity> existing = repository.findDetailedByIdempotencyKey(normalizedKey);
        if (existing.isPresent()) {
            return existing.get();
        }
        if (request.items().size() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O pedido pode conter no máximo 100 linhas de itens");
        }
        List<String> skus = request.items().stream().map(line -> line.sku().trim())
                .distinct().toList();
        if (skus.size() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A consulta ao catálogo pode conter no máximo 100 SKUs distintos");
        }
        List<CatalogProduct> products = catalogClient.findActiveProducts(skus);
        Map<String, CatalogProduct> bySku = products.stream()
                .filter(CatalogProduct::active)
                .collect(Collectors.toMap(CatalogProduct::sku, Function.identity(), (a, b) -> a));

        if (bySku.size() < skus.size()) {
            Set<String> missing = new LinkedHashSet<>(skus);
            missing.removeAll(bySku.keySet());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Produto inexistente ou inativo: " + String.join(", ", missing));
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItemEntity> items = new ArrayList<>();
        for (var line : request.items()) {
            CatalogProduct product = bySku.get(line.sku().trim());
            BigDecimal unitPrice = product.price().setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(line.quantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(lineTotal);
            items.add(new OrderItemEntity(product.sku(), product.name(), line.quantity(),
                    unitPrice, lineTotal));
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = subtotal.multiply(discountProperties.percent())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        OrderEntity order = new OrderEntity(request.customerName().trim(), normalizedKey,
                subtotal, discount, total, OrderStatus.CREATED);
        items.forEach(order::addItem);
        try {
            return repository.saveAndFlush(order);
        } catch (DataIntegrityViolationException exception) {
            // Race: another request with the same idempotency key won the insert.
            return repository.findDetailedByIdempotencyKey(normalizedKey).orElseThrow(() -> exception);
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderEntity> findAll(Pageable pageable) {
        return repository.findAllWithItems(pageable);
    }

    @Transactional(readOnly = true)
    public OrderEntity findById(long id) {
        return repository.findDetailedById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private String normalizeIdempotencyKey(String value) {
        if (value == null || value.isBlank() || value.length() > 128) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O header Idempotency-Key é obrigatório e deve possuir até 128 caracteres");
        }
        return value.trim();
    }
}
