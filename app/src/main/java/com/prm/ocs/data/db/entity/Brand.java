package com.prm.ocs.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(tableName = "brands")
public class Brand {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "brandId")
    private UUID brandId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    // Constructor
    public Brand() {
        this.brandId = UUID.randomUUID();
    }

    public Brand(UUID uuid, String name, String description) {
        this.brandId = uuid;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
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
}