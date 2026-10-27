package com.example.catalog.service;

import com.example.catalog.api.ProductDtos.CreateProductRequest;
import com.example.catalog.domain.Product;
import com.example.catalog.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DuplicateKeyException;

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
        try {
            return repository.save(new Product(sku, request.name().trim(), request.description(),
                    request.price(), request.active()));
        } catch (DuplicateKeyException exception) {
            // Handles the race where two requests pass existsBySku simultaneously.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU já cadastrado", exception);
        }
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
        if (skus.size() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A consulta aceita no máximo 100 SKUs distintos");
        }
        return repository.findBySkuInAndActiveTrue(skus);
    }
}
