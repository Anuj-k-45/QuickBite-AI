package com.quickbite.search.core.features.searchitems;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;

@Component
public class SearchMenuItemsQueryHandler
        implements IQueryHandler<SearchMenuItemsQuery, SearchMenuItemsResult> {

    private final MenuItemSearchQueryRepository searchRepository;

    public SearchMenuItemsQueryHandler(
            MenuItemSearchQueryRepository searchRepository) {

        this.searchRepository = searchRepository;
    }

    @Override
    public SearchMenuItemsResult handle(
            SearchMenuItemsQuery query) {

        Page<SearchMenuItemResultItem> result = searchRepository.search(
                query.query(),
                query.restaurantId(),
                query.category(),
                query.isVeg(),
                query.available(),
                query.minPrice(),
                query.maxPrice(),
                query.page(),
                query.size());

        return new SearchMenuItemsResult(
                result.getContent(),
                result.getTotalElements(),
                result.getNumber(),
                result.getSize());
    }
}