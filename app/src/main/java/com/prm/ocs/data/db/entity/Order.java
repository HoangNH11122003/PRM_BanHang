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
        tableName = "orders",
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "userId"),
        indices = @Index(value = "userId")
)
public class Order {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "orderId")
    private UUID orderId;

    @ColumnInfo(name = "userId")
    private UUID userId;

    @ColumnInfo(name = "orderDate")
    private Date orderDate;

    @ColumnInfo(name = "totalAmount")
    private double totalAmount;

    @ColumnInfo(name = "address")
    private String address;



    //Status
    @ColumnInfo(name = "status")
    private String status;

    // Constructor
    public Order() {
        this.orderId = UUID.randomUUID();
    }

    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
