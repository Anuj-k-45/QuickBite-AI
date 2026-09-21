package com.quickbite.search.core.repositories;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.quickbite.search.core.documents.RestaurantSearchDocument;

public interface RestaurantSearchRepository
        extends ElasticsearchRepository<RestaurantSearchDocument, UUID> {
}