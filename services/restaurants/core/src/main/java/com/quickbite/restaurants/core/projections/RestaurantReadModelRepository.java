package com.quickbite.restaurants.core.projections;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantReadModelRepository extends MongoRepository<RestaurantReadModel, UUID> {
}