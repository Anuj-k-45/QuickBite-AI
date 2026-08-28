package com.quickbite.orders.core.orders.features.gettingorderbyid;

import com.quickbite.buildingblocks.mediator.abstractions.IQuery;
import java.util.UUID;

public record GetOrderByIdQuery(UUID orderId) implements IQuery<OrderDetailsDto> {
}