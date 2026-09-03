package com.quickbite.orders.api.controllers;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.orders.core.orders.features.assigningdriver.AssignDriverCommand;
import com.quickbite.orders.core.orders.features.creatingorder.CreateOrderCommand;
import com.quickbite.orders.core.orders.features.creatingorder.CreateOrderResult;
import com.quickbite.orders.core.orders.features.gettingorderbyid.GetOrderByIdQuery;
import com.quickbite.orders.core.orders.features.gettingorderbyid.OrderDetailsDto;
import com.quickbite.orders.core.orders.features.updatingstatus.UpdateOrderStatusCommand;
import com.quickbite.orders.core.orders.model.OrderStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders API", description = "Order Management Endpoints")
public class OrdersController {

    private final Mediator mediator;

    public OrdersController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    @Operation(summary = "Place a new order")
    public ResponseEntity<CreateOrderResult> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderCommand requestBody) {

        String customerPhone = userDetails != null ? userDetails.getUsername() : null;

        if (customerPhone == null) {
            throw new RuntimeException("Unauthorized: Customer identity could not be extracted");
        }

        UUID customerId = UUID.nameUUIDFromBytes(customerPhone.getBytes());

        // Pass all required fields matching the updated CreateOrderCommand record
        CreateOrderCommand command = new CreateOrderCommand(
                customerId,
                requestBody.restaurantId(),
                requestBody.deliveryAddress(),
                requestBody.deliveryLatitude(),
                requestBody.deliveryLongitude(),
                requestBody.items());

        CreateOrderResult result = mediator.send(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details by ID from read-model")
    public ResponseEntity<OrderDetailsDto> getOrderById(@PathVariable UUID id) {
        OrderDetailsDto result = mediator.send(new GetOrderByIdQuery(id));
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status (PENDING, CONFIRMED, PREPARING, DELIVERED, CANCELLED)")
    public ResponseEntity<OrderStatusUpdateResponse> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest request) {

        mediator.send(new UpdateOrderStatusCommand(id, request.status()));

        OrderStatusUpdateResponse response = new OrderStatusUpdateResponse(
                id,
                request.status(),
                "Order status successfully updated",
                Instant.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/assign-driver")
    @Operation(summary = "Assign a driver to an order")
    public ResponseEntity<Void> assignDriver(
            @PathVariable UUID id,
            @Valid @RequestBody DriverAssignRequest request) {
        mediator.send(new AssignDriverCommand(id, request.driverId()));
        return ResponseEntity.ok().build();
    }

    public record StatusUpdateRequest(@NotNull OrderStatus status) {
    }

    public record DriverAssignRequest(@NotNull UUID driverId) {
    }

    public record OrderStatusUpdateResponse(
            UUID orderId,
            OrderStatus status,
            String message,
            Instant updatedAt) {
    }
}