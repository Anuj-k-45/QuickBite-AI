package com.quickbite.orders.core.orders.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant lastModifiedAt;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_latitude", nullable = false)
    private Double deliveryLatitude;

    @Column(name = "delivery_longitude", nullable = false)
    private Double deliveryLongitude;

    @Version
    private Long version;

    protected Order() {
    }

    // --- Added Constructor for Geospatial Delivery Information ---
    public Order(UUID id, UUID customerId, UUID restaurantId, String deliveryAddress, Double deliveryLatitude,
            Double deliveryLongitude, OrderStatus status, List<OrderItem> items) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        this.deliveryLatitude = deliveryLatitude;
        this.deliveryLongitude = deliveryLongitude;
        this.status = status;
        this.items = items != null ? items : new ArrayList<>();
        this.totalPrice = this.items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.createdAt = Instant.now();
        this.lastModifiedAt = Instant.now();
    }

    // Retained old constructor just in case other parts of the code use it
    public Order(UUID id, UUID customerId, UUID restaurantId, OrderStatus status, List<OrderItem> items) {
        this(id, customerId, restaurantId, "Default Address", 0.0, 0.0, status, items);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public Double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public Double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
        this.lastModifiedAt = Instant.now();
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        this.lastModifiedAt = Instant.now();
    }
}