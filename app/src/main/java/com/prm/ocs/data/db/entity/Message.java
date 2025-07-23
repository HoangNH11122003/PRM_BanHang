package com.prm.ocs.data.db.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.Date;

@Entity(
        tableName = "messages",
        foreignKeys = {
                @ForeignKey(entity = Conversation.class, parentColumns = "id", childColumns = "conversationId"),
                @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "senderId")
        },
        indices = {
                @Index(value = "conversationId"),
                @Index(value = "senderId")
        }
)
public class Message {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    private UUID id;

    @ColumnInfo(name = "conversationId")
    private UUID conversationId;

    @ColumnInfo(name = "senderId")
    private UUID senderId;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "createAt")
    private Date createAt;

    // Constructor
    public Message() {
        this.id = UUID.randomUUID();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}




























































































































