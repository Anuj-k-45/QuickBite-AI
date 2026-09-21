package com.quickbite.search.core.repositories;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.quickbite.search.core.documents.MenuItemSearchDocument;

public interface MenuItemSearchRepository
        extends ElasticsearchRepository<MenuItemSearchDocument, UUID> {
}