package com.prm.ocs.data.dto;

import com.prm.ocs.data.db.entity.Order;

import java.util.UUID;

public class OrderSummaryDto {
    private Order order;
    private int productCount;
    private double totalAmount;

    public OrderSummaryDto(Order order, int productCount, double totalAmount) {
        this.order = order;
        this.productCount = productCount;
        this.totalAmount = totalAmount;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}