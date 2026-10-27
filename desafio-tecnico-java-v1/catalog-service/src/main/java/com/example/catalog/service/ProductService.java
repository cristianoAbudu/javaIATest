package com.example.catalog.service;

import com.example.catalog.api.ProductDtos.CreateProductRequest;
import com.example.catalog.domain.Product;
import com.example.catalog.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product create(CreateProductRequest request) {
        String sku = request.sku().trim();
        if (repository.existsBySku(sku)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU já cadastrado");
        }
        return repository.save(new Product(sku, request.name().trim(), request.description(),
                request.price(), request.active()));
    }

    public Product findBySku(String sku) {
        return repository.findBySkuAndActiveTrue(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto inexistente ou inativo"));
    }

    public List<Product> findBySkus(String rawSkus) {
        List<String> skus = Arrays.stream(rawSkus.split(","))
                .map(String::trim).filter(value -> !value.isBlank()).distinct().toList();
        if (skus.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe ao menos um SKU no parâmetro skus");
        }
        return repository.findBySkuInAndActiveTrue(skus);
    }
}
