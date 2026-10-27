package com.example.orders.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "orders.discount")
public record DiscountProperties(
        @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal percent) {
    public DiscountProperties {
        if (percent == null) {
            percent = BigDecimal.ZERO;
        }
    }
}
