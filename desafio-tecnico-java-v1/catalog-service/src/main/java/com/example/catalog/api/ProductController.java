package com.example.catalog.api;

import com.example.catalog.api.ProductDtos.CreateProductRequest;
import com.example.catalog.api.ProductDtos.ProductResponse;
import com.example.catalog.service.ProductService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        var product = service.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + product.getSku()))
                .body(ProductResponse.from(product));
    }

    @GetMapping("/{sku}")
    public ProductResponse get(@PathVariable String sku) {
        return ProductResponse.from(service.findBySku(sku));
    }

    @GetMapping
    public List<ProductResponse> getBatch(@RequestParam String skus) {
        return service.findBySkus(skus).stream().map(ProductResponse::from).toList();
    }
}
