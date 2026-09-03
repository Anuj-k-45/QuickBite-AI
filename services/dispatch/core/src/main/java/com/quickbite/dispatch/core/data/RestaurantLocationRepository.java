package com.quickbite.dispatch.core.data;

import com.quickbite.dispatch.core.model.RestaurantLocationReadModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantLocationRepository extends MongoRepository<RestaurantLocationReadModel, UUID> {
}