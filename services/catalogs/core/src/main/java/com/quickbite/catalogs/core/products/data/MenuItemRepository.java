package com.quickbite.catalogs.core.products.data;

import com.quickbite.catalogs.core.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(UUID restaurantId);
}