package com.quickbite.catalogs.core.products.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
public class RestaurantRef {
    @Id
    private UUID id;

    @Column(name = "owner_phone")
    private String ownerPhone;

    public UUID getId() {
        return id;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
}