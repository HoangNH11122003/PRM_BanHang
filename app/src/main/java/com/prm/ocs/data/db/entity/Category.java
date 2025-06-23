package com.prm.ocs.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "categoryId")
    private UUID categoryId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    // Constructor
    public Category() {
        this.categoryId = UUID.randomUUID();
    }

    public Category(UUID uuid, String s, String description) {
        this.categoryId = uuid;
        this.name = s;
        this.description = description;
    }

    // Getters and Setters
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
}
