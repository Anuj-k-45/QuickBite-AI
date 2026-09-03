package com.quickbite.orders.core.orders.features.assigningdriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.orders.core.orders.data.OrderRepository;
import com.quickbite.orders.core.orders.model.Order;
import com.quickbite.orders.core.orders.model.OrderStatus;

@Service
public class AssignDriverCommandHandler implements ICommandHandler<AssignDriverCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(AssignDriverCommandHandler.class);
    private final OrderRepository orderRepository;

    public AssignDriverCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Void handle(AssignDriverCommand command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + command.orderId()));

        order.setDriverId(command.driverId());
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);

        log.info("[DRIVER ASSIGNMENT] Driver {} assigned to order {}", command.driverId(), command.orderId());
        return null;
    }
}