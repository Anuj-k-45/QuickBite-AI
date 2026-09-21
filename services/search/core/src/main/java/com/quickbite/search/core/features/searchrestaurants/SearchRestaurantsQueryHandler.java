package com.quickbite.search.core.features.searchrestaurants;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;

@Component
public class SearchRestaurantsQueryHandler
        implements IQueryHandler<SearchRestaurantsQuery, SearchRestaurantsResult> {

    private final RestaurantSearchQueryRepository searchRepository;

    public SearchRestaurantsQueryHandler(
            RestaurantSearchQueryRepository searchRepository) {

        this.searchRepository = searchRepository;
    }

    @Override
    public SearchRestaurantsResult handle(
            SearchRestaurantsQuery query) {

        Page<RestaurantSearchResultItem> result = searchRepository.search(
                query.query(),
                query.cuisine(),
                query.open(),
                query.minRating(),
                query.maxCost(),
                query.latitude(),
                query.longitude(),
                query.radius(),
                query.sort(),
                query.page(),
                query.size());

        return new SearchRestaurantsResult(
                result.getContent(),
                result.getTotalElements(),
                result.getNumber(),
                result.getSize());
    }
}