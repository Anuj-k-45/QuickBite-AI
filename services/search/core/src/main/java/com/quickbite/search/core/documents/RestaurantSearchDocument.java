package com.quickbite.search.core.documents;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Document(indexName = "restaurants")
public class RestaurantSearchDocument {

    @Id
    private UUID id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String cuisineType;

    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Boolean)
    private boolean isOpen;

    @Field(type = FieldType.Keyword)
    private String imageUrl;

    @Field(type = FieldType.Double)
    private Double rating;

    @Field(type = FieldType.Integer)
    private Integer deliveryTimeMinutes;

    @Field(type = FieldType.Double)
    private BigDecimal costForTwo;

    private GeoPoint location;

    public RestaurantSearchDocument() {
    }

    public RestaurantSearchDocument(
            UUID id,
            String name,
            String description,
            String cuisineType,
            String address,
            boolean isOpen,
            String imageUrl,
            Double rating,
            Integer deliveryTimeMinutes,
            BigDecimal costForTwo,
            GeoPoint location) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.cuisineType = cuisineType;
        this.address = address;
        this.isOpen = isOpen;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.deliveryTimeMinutes = deliveryTimeMinutes;
        this.costForTwo = costForTwo;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getDeliveryTimeMinutes() {
        return deliveryTimeMinutes;
    }

    public void setDeliveryTimeMinutes(Integer deliveryTimeMinutes) {
        this.deliveryTimeMinutes = deliveryTimeMinutes;
    }

    public BigDecimal getCostForTwo() {
        return costForTwo;
    }

    public void setCostForTwo(BigDecimal costForTwo) {
        this.costForTwo = costForTwo;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}