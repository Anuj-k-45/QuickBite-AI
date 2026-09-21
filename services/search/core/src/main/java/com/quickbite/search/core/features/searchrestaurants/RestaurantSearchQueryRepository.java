package com.quickbite.search.core.features.searchrestaurants;

import com.quickbite.search.core.documents.RestaurantSearchDocument;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestaurantSearchQueryRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public RestaurantSearchQueryRepository(
            ElasticsearchOperations elasticsearchOperations) {

        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Page<RestaurantSearchResultItem> search(
            String searchText,
            String cuisine,
            Boolean open,
            Double minRating,
            Double maxCost,
            Double latitude,
            Double longitude,
            Double radius,
            String sort,
            int page,
            int size) {

        PageRequest pageable = PageRequest.of(page, size);

        boolean hasGeoFilter = latitude != null
                && longitude != null
                && radius != null;

        boolean hasTextSearch = searchText != null
                && !searchText.isBlank();

        boolean hasFilters = cuisine != null
                || open != null
                || minRating != null
                || maxCost != null
                || hasGeoFilter;

        boolean sortByDistance = "distance".equalsIgnoreCase(sort)
                && latitude != null
                && longitude != null;

        Query query;

        /*
         * CASE 1:
         * No text search and no filters.
         */
        if (!hasTextSearch && !hasFilters) {

            if (sortByDistance) {

                query = NativeQuery.builder()
                        .withQuery(q -> q.matchAll(m -> m))
                        .withPageable(pageable)
                        .withSort(s -> s.geoDistance(g -> g
                                .field("location")
                                .location(l -> l
                                        .latlon(ll -> ll
                                                .lat(latitude)
                                                .lon(longitude)))
                                .order(
                                        co.elastic.clients.elasticsearch._types.SortOrder.Asc)
                                .unit(
                                        co.elastic.clients.elasticsearch._types.DistanceUnit.Kilometers)))
                        .build();

            } else {

                query = NativeQuery.builder()
                        .withQuery(q -> q.matchAll(m -> m))
                        .withPageable(pageable)
                        .build();
            }

        }

        /*
         * CASE 2:
         * Text search and/or filters.
         */
        else {

            if (sortByDistance) {

                query = NativeQuery.builder()
                        .withQuery(q -> q.bool(b -> {

                            addSearchCriteria(
                                    b,
                                    hasTextSearch,
                                    searchText,
                                    cuisine,
                                    open,
                                    minRating,
                                    maxCost,
                                    hasGeoFilter,
                                    latitude,
                                    longitude,
                                    radius);

                            return b;
                        }))
                        .withPageable(pageable)
                        .withSort(s -> s.geoDistance(g -> g
                                .field("location")
                                .location(l -> l
                                        .latlon(ll -> ll
                                                .lat(latitude)
                                                .lon(longitude)))
                                .order(
                                        co.elastic.clients.elasticsearch._types.SortOrder.Asc)
                                .unit(
                                        co.elastic.clients.elasticsearch._types.DistanceUnit.Kilometers)))
                        .build();

            } else {

                query = NativeQuery.builder()
                        .withQuery(q -> q.bool(b -> {

                            addSearchCriteria(
                                    b,
                                    hasTextSearch,
                                    searchText,
                                    cuisine,
                                    open,
                                    minRating,
                                    maxCost,
                                    hasGeoFilter,
                                    latitude,
                                    longitude,
                                    radius);

                            return b;
                        }))
                        .withPageable(pageable)
                        .build();
            }
        }

        SearchHits<RestaurantSearchDocument> searchHits = elasticsearchOperations.search(
                query,
                RestaurantSearchDocument.class);

        List<RestaurantSearchResultItem> restaurants = searchHits.getSearchHits()
                .stream()
                .map(hit -> toResultItem(
                        hit,
                        latitude,
                        longitude))
                .toList();

        return new PageImpl<>(
                restaurants,
                pageable,
                searchHits.getTotalHits());
    }

    private void addSearchCriteria(
            co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder b,
            boolean hasTextSearch,
            String searchText,
            String cuisine,
            Boolean open,
            Double minRating,
            Double maxCost,
            boolean hasGeoFilter,
            Double latitude,
            Double longitude,
            Double radius) {

        if (hasTextSearch) {

            b.should(s -> s.multiMatch(m -> m
                    .query(searchText)
                    .fields(
                            "name^4",
                            "description^2",
                            "cuisineType^3",
                            "address")));

            b.should(s -> s.multiMatch(m -> m
                    .query(searchText)
                    .fields(
                            "name^4",
                            "description^2",
                            "cuisineType^3",
                            "address")
                    .fuzziness("AUTO")
                    .prefixLength(0)));

            b.minimumShouldMatch("1");
        }

        if (cuisine != null
                && !cuisine.isBlank()) {

            b.filter(f -> f.term(t -> t
                    .field("cuisineType")
                    .value(cuisine)));
        }

        if (open != null) {

            b.filter(f -> f.term(t -> t
                    .field("isOpen")
                    .value(open)));
        }

        if (minRating != null) {

            b.filter(f -> f.range(r -> r
                    .number(n -> n
                            .field("rating")
                            .gte(minRating))));
        }

        if (maxCost != null) {

            b.filter(f -> f.range(r -> r
                    .number(n -> n
                            .field("costForTwo")
                            .lte(maxCost))));
        }

        if (hasGeoFilter) {

            b.filter(f -> f.geoDistance(g -> g
                    .field("location")
                    .location(l -> l
                            .latlon(ll -> ll
                                    .lat(latitude)
                                    .lon(longitude)))
                    .distance(radius + "km")));
        }
    }

    private RestaurantSearchResultItem toResultItem(
            SearchHit<RestaurantSearchDocument> hit,
            Double userLatitude,
            Double userLongitude) {

        RestaurantSearchDocument restaurant = hit.getContent();

        Double distanceKm = null;

        /*
         * Calculate distance only when the user has provided
         * their location.
         */
        if (userLatitude != null
                && userLongitude != null
                && restaurant.getLocation() != null) {

            distanceKm = calculateDistanceKm(
                    userLatitude,
                    userLongitude,
                    restaurant.getLocation().getLat(),
                    restaurant.getLocation().getLon());
        }

        return new RestaurantSearchResultItem(
                restaurant,
                distanceKm);
    }

    private double calculateDistanceKm(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2) {

        final double EARTH_RADIUS_KM = 6371.0;

        double latitude1Radians = Math.toRadians(latitude1);

        double latitude2Radians = Math.toRadians(latitude2);

        double deltaLatitude = Math.toRadians(latitude2 - latitude1);

        double deltaLongitude = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(deltaLatitude / 2)
                * Math.sin(deltaLatitude / 2)
                + Math.cos(latitude1Radians)
                        * Math.cos(latitude2Radians)
                        * Math.sin(deltaLongitude / 2)
                        * Math.sin(deltaLongitude / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        return Math.round(distance * 100.0) / 100.0;
    }
}