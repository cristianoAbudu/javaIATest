package com.example.orders.catalog;

import java.util.Arrays;
import java.util.List;
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

    public List<CatalogProduct> findActiveProducts(List<String> skus) {
        try {
            CatalogProduct[] response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/products")
                            .queryParam("skus", String.join(",", skus)).build())
                    .retrieve()
                    .body(CatalogProduct[].class);
            return response == null ? List.of() : Arrays.asList(response);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível consultar o catálogo", exception);
        }
    }
}
