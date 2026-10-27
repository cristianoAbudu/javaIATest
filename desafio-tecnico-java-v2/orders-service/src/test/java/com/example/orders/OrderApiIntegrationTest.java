package com.example.orders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.orders.catalog.CatalogClient;
import com.example.orders.domain.OrderEntity;
import com.example.orders.domain.OrderStatus;
import com.example.orders.repository.OrderRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class OrderApiIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("orders")
            .withUsername("orders")
            .withPassword("orders");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired MockMvc mockMvc;
    @Autowired OrderRepository repository;
    @MockBean CatalogClient catalogClient;

    @Test
    void getOrdersLoadsItemsWithOpenInViewDisabled() throws Exception {
        OrderEntity order = new OrderEntity("Cliente", "integration-idem-1",
                new BigDecimal("20.00"), new BigDecimal("2.00"),
                new BigDecimal("18.00"), OrderStatus.CREATED);
        order.addItem(new com.example.orders.domain.OrderItemEntity(
                "SKU-1", "Produto", 2, new BigDecimal("10.00"), new BigDecimal("20.00")));
        repository.saveAndFlush(order);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].items[0].sku").value("SKU-1"))
                .andExpect(jsonPath("$.content[0].total").value(18.00));
    }
}
