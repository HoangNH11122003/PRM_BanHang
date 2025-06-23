package com.prm.ocs.data.dto;

import com.prm.ocs.data.db.entity.Category;

import java.util.UUID;

public class CategoryProductsDto {
    private UUID categoryId;
    private String name;
    private String description;
    private int numberOfProducts;

    public CategoryProductsDto(Category category, int numberOfProducts) {
        this.categoryId = category.getCategoryId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.numberOfProducts = numberOfProducts;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
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