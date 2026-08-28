package com.quickbite.catalogs.core.products.features.gettingitems;

import com.quickbite.buildingblocks.mediator.abstractions.IQuery;
import com.quickbite.catalogs.core.projections.CatalogReadModel;
import java.util.List;

public record GetCatalogItemsQuery(String category) implements IQuery<List<CatalogReadModel>> {
}