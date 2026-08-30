package com.quickbite.catalogs.core.products.data;

import com.quickbite.catalogs.core.products.model.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CatalogItemRepository extends JpaRepository<CatalogItem, UUID> {

    @Query("SELECT c FROM CatalogItem c WHERE c.restaurantId = :restaurantId AND c.available = true")
    List<CatalogItem> findByRestaurantIdAndIsAvailableTrue(@Param("restaurantId") UUID restaurantId);
}