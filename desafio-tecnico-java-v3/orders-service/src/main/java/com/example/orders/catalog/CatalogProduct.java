package com.example.orders.catalog;

import java.math.BigDecimal;

public record CatalogProduct(String sku, String name, BigDecimal price, boolean active) {}
