package com.quickbite.search.core.features.searchitems;

import java.util.List;

public record SearchMenuItemsResult(
        List<SearchMenuItemResultItem> menuItems,
        long totalElements,
        int page,
        int size) {
}