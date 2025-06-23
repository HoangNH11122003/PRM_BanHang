package com.prm.ocs.data.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartDTO implements Serializable {
    private List<CartItemDTO> items;

    public CartDTO() {
        this.items = new ArrayList<>();
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void addItem(UUID productId, int quantity) { // Thay String bằng UUID
        for (CartItemDTO item : items) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItemDTO(productId, quantity));
    }

    public void removeItem(UUID productId) { // Thay String bằng UUID
        items.removeIf(item -> item.getProductId().equals(productId));
    }
}