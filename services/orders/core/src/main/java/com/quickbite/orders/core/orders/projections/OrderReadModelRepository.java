package com.quickbite.orders.core.orders.projections;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderReadModelRepository extends MongoRepository<OrderReadModel, UUID> {
}