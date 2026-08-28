package com.quickbite.orders.core.orders.model;

import com.quickbite.buildingblocks.domain.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@jakarta.persistence.Entity
@Table(name = "order_items")
public class OrderItem extends Entity<UUID> {

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    protected OrderItem() {
    }

    public OrderItem(UUID id, UUID productId, String productName, BigDecimal unitPrice, int quantity) {
        super(id);
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}