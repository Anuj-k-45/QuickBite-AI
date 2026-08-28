package com.quickbite.orders.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.orders.core.orders.features.creatingorder.CreateOrderCommand;
import com.quickbite.orders.core.orders.features.creatingorder.CreateOrderResult;
import com.quickbite.orders.core.orders.features.gettingorderbyid.GetOrderByIdQuery;
import com.quickbite.orders.core.orders.features.gettingorderbyid.OrderDetailsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public ResponseEntity<CreateOrderResult> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        CreateOrderResult result = mediator.send(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details by ID from read-model")
    public ResponseEntity<OrderDetailsDto> getOrderById(@PathVariable UUID id) {
        OrderDetailsDto result = mediator.send(new GetOrderByIdQuery(id));
        return ResponseEntity.ok(result);
    }
}