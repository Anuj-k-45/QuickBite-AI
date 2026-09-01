package com.quickbite.catalogs.core.products.features.creatingitem;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateCatalogItemCommand(
                @NotBlank(message = "Item name is required") String name,

                String description,

                @NotNull(message = "Price is required") @DecimalMin(value = "0.01", message = "Price must be greater than 0") BigDecimal price,

                @NotBlank(message = "Category is required") String category,

                String imageUrl,
                boolean isVeg,
                boolean bestseller) implements ICommand<CreateCatalogItemResult> {
}