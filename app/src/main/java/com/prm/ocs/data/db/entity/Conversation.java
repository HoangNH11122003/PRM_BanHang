package com.prm.ocs.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(tableName = "conversations")
public class Conversation {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    private UUID id;

    @ColumnInfo(name = "userGuid")
    private UUID userGuid;

    @ColumnInfo(name = "adminGuid")
    private UUID adminGuid;

    // Constructor
    public Conversation() {
        this.id = UUID.randomUUID();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(UUID userGuid) {
        this.userGuid = userGuid;
    }

    public UUID getAdminGuid() {
        return adminGuid;
    }

    public void setAdminGuid(UUID adminGuid) {
        this.adminGuid = adminGuid;
    }
}