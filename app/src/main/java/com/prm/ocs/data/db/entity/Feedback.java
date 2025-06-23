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
        tableName = "feedbacks",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "userId"),
                @ForeignKey(entity = Product.class, parentColumns = "productId", childColumns = "productId")
        },
        indices = {
                @Index(value = "userId"),
                @Index(value = "productId")
        }
)
public class Feedback {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "feedbackId")
    private UUID feedbackId;

    @ColumnInfo(name = "userId")
    private UUID userId;

    @ColumnInfo(name = "productId")
    private UUID productId;

    @ColumnInfo(name = "rating")
    private int rating;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "createdAt")
    private Date createdAt;

    // Constructor
    public Feedback() {
        this.feedbackId = UUID.randomUUID();
    }

    // Getters and Setters
    public UUID getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(UUID feedbackId) {
        this.feedbackId = feedbackId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
