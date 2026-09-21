package com.quickbite.search.core.features.searchitems;

import com.quickbite.search.core.documents.MenuItemSearchDocument;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MenuItemSearchQueryRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public MenuItemSearchQueryRepository(
            ElasticsearchOperations elasticsearchOperations) {

        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Page<SearchMenuItemResultItem> search(
            String searchText,
            UUID restaurantId,
            String category,
            Boolean isVeg,
            Boolean available,
            Double minPrice,
            Double maxPrice,
            int page,
            int size) {

        PageRequest pageable = PageRequest.of(page, size);

        boolean hasTextSearch = searchText != null
                && !searchText.isBlank();

        boolean hasFilters = restaurantId != null
                || (category != null && !category.isBlank())
                || isVeg != null
                || available != null
                || minPrice != null
                || maxPrice != null;

        Query query;

        /*
         * CASE 1:
         * No text search and no filters.
         *
         * Return all menu items.
         */
        if (!hasTextSearch && !hasFilters) {

            query = NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(pageable)
                    .build();
        }

        /*
         * CASE 2:
         * Text search and/or filters.
         */
        else {

            query = NativeQuery.builder()
                    .withQuery(q -> q.bool(b -> {

                        addSearchCriteria(
                                b,
                                hasTextSearch,
                                searchText,
                                restaurantId,
                                category,
                                isVeg,
                                available,
                                minPrice,
                                maxPrice);

                        return b;
                    }))
                    .withPageable(pageable)
                    .build();
        }

        SearchHits<MenuItemSearchDocument> searchHits = elasticsearchOperations.search(
                query,
                MenuItemSearchDocument.class);

        List<SearchMenuItemResultItem> menuItems = searchHits.getSearchHits()
                .stream()
                .map(this::toResultItem)
                .toList();

        return new PageImpl<>(
                menuItems,
                pageable,
                searchHits.getTotalHits());
    }

    private void addSearchCriteria(
            co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder b,
            boolean hasTextSearch,
            String searchText,
            UUID restaurantId,
            String category,
            Boolean isVeg,
            Boolean available,
            Double minPrice,
            Double maxPrice) {

        /*
         * TEXT SEARCH
         *
         * Search the item name and description.
         *
         * Name receives a higher boost because it is
         * generally more relevant to what the user typed.
         */
        if (hasTextSearch) {

            b.should(s -> s.multiMatch(m -> m
                    .query(searchText)
                    .fields(
                            "name^4",
                            "description^2",
                            "category^3")));

            b.should(s -> s.multiMatch(m -> m
                    .query(searchText)
                    .fields(
                            "name^4",
                            "description^2",
                            "category^3")
                    .fuzziness("AUTO")
                    .prefixLength(0)));

            b.minimumShouldMatch("1");
        }

        /*
         * RESTAURANT FILTER
         *
         * Exact match because restaurantId is a Keyword field.
         */
        if (restaurantId != null) {

            b.filter(f -> f.term(t -> t
                    .field("restaurantId")
                    .value(restaurantId.toString())));
        }

        /*
         * CATEGORY FILTER
         *
         * Exact match because category is a Keyword field.
         */
        if (category != null
                && !category.isBlank()) {

            b.filter(f -> f.term(t -> t
                    .field("category")
                    .value(category)));
        }

        /*
         * VEGETARIAN FILTER
         */
        if (isVeg != null) {

            b.filter(f -> f.term(t -> t
                    .field("isVeg")
                    .value(isVeg)));
        }

        /*
         * AVAILABILITY FILTER
         */
        if (available != null) {

            b.filter(f -> f.term(t -> t
                    .field("available")
                    .value(available)));
        }

        /*
         * MINIMUM PRICE
         */
        if (minPrice != null) {

            b.filter(f -> f.range(r -> r
                    .number(n -> n
                            .field("price")
                            .gte(minPrice))));
        }

        /*
         * MAXIMUM PRICE
         */
        if (maxPrice != null) {

            b.filter(f -> f.range(r -> r
                    .number(n -> n
                            .field("price")
                            .lte(maxPrice))));
        }
    }

    private SearchMenuItemResultItem toResultItem(
            SearchHit<MenuItemSearchDocument> hit) {

        return new SearchMenuItemResultItem(
                hit.getContent());
    }
}