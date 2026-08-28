package com.quickbite.orders.core.orders.features.gettingorderbyid;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;
import com.quickbite.orders.core.orders.projections.OrderReadModel;
import com.quickbite.orders.core.orders.projections.OrderReadModelRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class GetOrderByIdQueryHandler implements IQueryHandler<GetOrderByIdQuery, OrderDetailsDto> {

        private final OrderReadModelRepository repository;

        public GetOrderByIdQueryHandler(OrderReadModelRepository repository) {
                this.repository = repository;
        }

        @Override
        public OrderDetailsDto handle(GetOrderByIdQuery query) {
                OrderReadModel readModel = repository.findById(query.orderId())
                                .orElseThrow(() -> new RuntimeException("Order not found with id: " + query.orderId()));

                List<OrderDetailsDto.OrderItemDto> items = readModel.items().stream()
                                .map(i -> new OrderDetailsDto.OrderItemDto(
                                                i.productId(),
                                                i.productName(),
                                                i.unitPrice(),
                                                i.quantity(),
                                                i.unitPrice().multiply(BigDecimal.valueOf(i.quantity()))))
                                .toList();

                return new OrderDetailsDto(
                                readModel.id(),
                                readModel.customerId(),
                                readModel.status(),
                                readModel.totalPrice(),
                                items,
                                readModel.createdAt());
        }
}