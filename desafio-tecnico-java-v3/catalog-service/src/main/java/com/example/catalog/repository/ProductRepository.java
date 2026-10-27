package com.example.catalog.repository;

import com.example.catalog.domain.Product;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findBySkuAndActiveTrue(String sku);
    List<Product> findBySkuInAndActiveTrue(Collection<String> skus);
    boolean existsBySku(String sku);
}
