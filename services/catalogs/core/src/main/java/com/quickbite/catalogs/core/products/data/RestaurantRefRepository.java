package com.quickbite.catalogs.core.products.data;

import com.quickbite.catalogs.core.products.model.RestaurantRef;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RestaurantRefRepository extends JpaRepository<RestaurantRef, UUID> {
}