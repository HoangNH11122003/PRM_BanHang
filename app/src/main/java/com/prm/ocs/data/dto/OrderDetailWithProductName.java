package com.prm.ocs.data.dto;

import com.prm.ocs.data.db.entity.OrderDetail;

import java.util.UUID;

public class OrderDetailWithProductName {
    private final UUID productId;
    private final int quantity;
    private final double price;
    private final String productName;

    public OrderDetailWithProductName(OrderDetail orderDetail, String productName) {
        this.productId = orderDetail.getProductId();
        this.quantity = orderDetail.getQuantity();
        this.price = orderDetail.getPrice();
        this.productName = productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }
}