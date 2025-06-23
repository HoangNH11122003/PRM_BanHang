package com.prm.ocs.ui.view.base;

import com.prm.ocs.data.db.entity.Product;
import java.util.List;

public interface ProductView {
    void displayProducts(List<Product> products); // Cho danh sách
    void displayProductDetails(Product product); // Cho chi tiết
}