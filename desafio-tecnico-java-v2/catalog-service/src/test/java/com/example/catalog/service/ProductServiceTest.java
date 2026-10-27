package com.example.catalog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.catalog.api.ProductDtos.CreateProductRequest;
import com.example.catalog.domain.Product;
import com.example.catalog.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock ProductRepository repository;
    @InjectMocks ProductService service;

    @Test
    void batchLookupUsesSingleRepositoryCall() {
        when(repository.findBySkuInAndActiveTrue(List.of("A", "B"))).thenReturn(List.of(
                new Product("A", "A", null, new BigDecimal("10.00"), true)));
        assertEquals(1, service.findBySkus("A,B").size());
        verify(repository, times(1)).findBySkuInAndActiveTrue(List.of("A", "B"));
    }

    @Test
    void rejectsMoreThanOneHundredDistinctSkus() {
        String skus = java.util.stream.IntStream.range(0, 101)
                .mapToObj(i -> "SKU-" + i).collect(java.util.stream.Collectors.joining(","));
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.findBySkus(skus));
        verify(repository, never()).findBySkuInAndActiveTrue(any());
    }

    @Test
    void rejectsDuplicateSku() {
        when(repository.existsBySku("A")).thenReturn(true);
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.create(new CreateProductRequest("A", "Produto", null,
                        new BigDecimal("10"), true)));
        verify(repository, never()).save(any());
    }
}
