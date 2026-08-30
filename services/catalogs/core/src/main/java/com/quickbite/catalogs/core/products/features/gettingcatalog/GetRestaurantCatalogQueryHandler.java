package com.quickbite.catalogs.core.products.features.gettingcatalog;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;
import com.quickbite.catalogs.core.products.data.CatalogItemRepository;
import com.quickbite.catalogs.core.products.model.CatalogItem;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GetRestaurantCatalogQueryHandler implements IQueryHandler<GetRestaurantCatalogQuery, List<CatalogItem>> {

    private final CatalogItemRepository catalogItemRepository;

    public GetRestaurantCatalogQueryHandler(CatalogItemRepository catalogItemRepository) {
        this.catalogItemRepository = catalogItemRepository;
    }

    @Override
    public List<CatalogItem> handle(GetRestaurantCatalogQuery query) {
        // Fallback check matching repository naming convention
        // (findByRestaurantIdAndIsAvailableTrue or active equivalent)
        return catalogItemRepository.findByRestaurantIdAndIsAvailableTrue(query.restaurantId());
    }
}