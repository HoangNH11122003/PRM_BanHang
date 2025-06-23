package com.prm.ocs.data.db.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(
        tableName = "order_details",
        foreignKeys = {
                @ForeignKey(entity = Order.class, parentColumns = "orderId", childColumns = "orderId"),
                @ForeignKey(entity = Product.class, parentColumns = "productId", childColumns = "productId")
        },
        indices = {
                @Index(value = "orderId"),
                @Index(value = "productId")
        }
)
public class OrderDetail {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "orderDetailId")
    private UUID orderDetailId;

    @ColumnInfo(name = "orderId")
    private UUID orderId;

    @ColumnInfo(name = "productId")
    private UUID productId;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "price")
    private double price;

    // Constructor
    public OrderDetail() {
        this.orderDetailId = UUID.randomUUID();
    }

    // Getters and Setters
    public UUID getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(UUID orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
