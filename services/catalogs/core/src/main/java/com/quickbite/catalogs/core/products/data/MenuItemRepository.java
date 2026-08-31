package com.quickbite.catalogs.core.products.data;

import com.quickbite.catalogs.core.products.model.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<CatalogItem, UUID> {
    List<CatalogItem> findByRestaurantIdAndAvailableTrue(UUID restaurantId);
}