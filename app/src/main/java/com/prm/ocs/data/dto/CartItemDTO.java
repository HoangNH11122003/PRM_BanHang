package com.prm.ocs.data.dto;

import java.io.Serializable;
import java.util.UUID;

public class CartItemDTO implements Serializable {
    private UUID productId;
    private int quantity;

    public CartItemDTO() {
    }

    public CartItemDTO(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
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
}