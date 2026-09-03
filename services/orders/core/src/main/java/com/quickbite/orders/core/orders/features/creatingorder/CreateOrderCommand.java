package com.quickbite.orders.core.orders.features.creatingorder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderCommand(
                UUID customerId,
                @NotNull(message = "Restaurant ID is required") UUID restaurantId,
                @NotEmpty(message = "Order must contain at least one item") @Valid List<OrderItemDto> items)
                implements ICommand<CreateOrderResult> {

        public record OrderItemDto(
                        @NotNull(message = "Product ID is required") UUID productId,
                        @NotBlank(message = "Product name cannot be empty") String productName,
                        @NotNull(message = "Unit price is required") @DecimalMin(value = "0.01", message = "Unit price must be greater than zero") BigDecimal unitPrice,
                        @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
        }
}