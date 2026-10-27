package com.example.catalog.api;

import com.example.catalog.domain.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public final class ProductDtos {
    private ProductDtos() {}

    public record CreateProductRequest(
            @NotBlank String sku,
            @NotBlank String name,
            String description,
            @NotNull @DecimalMin(value = "0.01") BigDecimal price,
            boolean active) {}

    public record ProductResponse(String sku, String name, String description,
                                  BigDecimal price, boolean active) {
        public static ProductResponse from(Product product) {
            return new ProductResponse(product.getSku(), product.getName(), product.getDescription(),
                    product.getPrice(), product.isActive());
        }
    }
}
