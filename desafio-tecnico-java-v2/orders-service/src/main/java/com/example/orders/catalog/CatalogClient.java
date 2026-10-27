package com.example.orders.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Arrays;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CatalogClient {
    private final RestClient restClient;

    public CatalogClient(RestClient catalogRestClient) {
        this.restClient = catalogRestClient;
    }

    @Retry(name = "catalogClient")
    @CircuitBreaker(name = "catalogClient")
    public List<CatalogProduct> findActiveProducts(List<String> skus) {
        try {
            var request = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/products")
                            .queryParam("skus", String.join(",", skus)).build());
            String correlationId = MDC.get("X-Correlation-Id");
            if (correlationId != null && !correlationId.isBlank()) {
                request.header("X-Correlation-Id", correlationId);
            }
            CatalogProduct[] response = request.retrieve().body(CatalogProduct[].class);
            return response == null ? List.of() : Arrays.asList(response);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível consultar o catálogo", exception);
        }
    }
}
