package com.quickbite.catalogs.core.products.data;

import com.quickbite.catalogs.core.products.model.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CatalogItemRepository extends JpaRepository<CatalogItem, UUID> {
    List<CatalogItem> findByActiveTrue();

    List<CatalogItem> findByCategoryIgnoreCaseAndActiveTrue(String category);
}