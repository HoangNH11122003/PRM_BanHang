package com.prm.ocs.data.dto;

import com.prm.ocs.data.db.entity.Brand;

import java.util.UUID;

public class BrandProductsDto {
    private UUID brandId;
    private String name;
    private String description;
    private int numberOfProducts;

    public BrandProductsDto(Brand brand, int numberOfProducts) {
        this.brandId = brand.getBrandId();
        this.name = brand.getName();
        this.description = brand.getDescription();
        this.numberOfProducts = numberOfProducts;
    }

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
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

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(int numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }
}