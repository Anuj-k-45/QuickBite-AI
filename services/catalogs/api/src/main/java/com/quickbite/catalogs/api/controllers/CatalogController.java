package com.quickbite.catalogs.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.catalogs.core.products.features.creatingitem.CreateCatalogItemCommand;
import com.quickbite.catalogs.core.products.features.creatingitem.CreateCatalogItemResult;
import com.quickbite.catalogs.core.products.features.gettingitems.GetCatalogItemsQuery;
import com.quickbite.catalogs.core.projections.CatalogReadModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/catalogs")
@Tag(name = "Catalogs API", description = "Product and Menu Management")
public class CatalogController {

    private final Mediator mediator;

    public CatalogController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    @Operation(summary = "Create a catalog menu item")
    public ResponseEntity<CreateCatalogItemResult> createItem(@Valid @RequestBody CreateCatalogItemCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.send(command));
    }

    @GetMapping
    @Operation(summary = "Get active menu catalog items")
    public ResponseEntity<List<CatalogReadModel>> getItems(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(mediator.send(new GetCatalogItemsQuery(category)));
    }
}