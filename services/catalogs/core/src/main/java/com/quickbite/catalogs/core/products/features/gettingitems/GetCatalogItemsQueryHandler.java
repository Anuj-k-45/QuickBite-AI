package com.quickbite.catalogs.core.products.features.gettingitems;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;
import com.quickbite.catalogs.core.projections.CatalogReadModel;
import com.quickbite.catalogs.core.projections.CatalogReadModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetCatalogItemsQueryHandler implements IQueryHandler<GetCatalogItemsQuery, List<CatalogReadModel>> {

    private static final Logger log = LoggerFactory.getLogger(GetCatalogItemsQueryHandler.class);

    private final CatalogReadModelRepository readModelRepository;
    private final MongoTemplate mongoTemplate;

    public GetCatalogItemsQueryHandler(CatalogReadModelRepository readModelRepository, MongoTemplate mongoTemplate) {
        this.readModelRepository = readModelRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<CatalogReadModel> handle(GetCatalogItemsQuery query) {
        log.info("[CQRS READ DEBUG] Querying MongoDB database: {}", mongoTemplate.getDb().getName());

        List<CatalogReadModel> results;
        if (query.category() != null && !query.category().isBlank()) {
            results = readModelRepository.findByCategoryIgnoreCase(query.category().trim());
        } else {
            results = readModelRepository.findAll();
        }

        log.info("[CQRS READ DEBUG] Found {} items in MongoDB collection 'catalog_read_models'", results.size());
        return results;
    }
}