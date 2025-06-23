package com.prm.ocs.data.dto;

import com.prm.ocs.data.db.entity.Product;

import java.io.Serializable;

public class CartItemViewDTO implements Serializable {
    private Product product;
    private int quantity;

    public CartItemViewDTO(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
