package com.quickbite.catalogs.core.projections;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogReadModelRepository extends MongoRepository<CatalogReadModel, String> {
    List<CatalogReadModel> findByCategoryIgnoreCase(String category);
}