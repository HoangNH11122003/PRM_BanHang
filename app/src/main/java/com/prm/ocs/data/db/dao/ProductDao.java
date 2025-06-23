package com.prm.ocs.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.prm.ocs.data.db.entity.Product;
import java.util.List;
import java.util.UUID;

@Dao
public interface ProductDao extends BaseDao<Product> {
    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("Select Count(*) FROM products")
    int getProductCount();

    @Query("SELECT * FROM products WHERE productId = :productId")
    Product getProductById(UUID productId);

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    List<Product> getProductsByCategory(UUID categoryId);

    @Query("SELECT * FROM products WHERE brandId = :brandId")
    List<Product> getProductsByBrand(UUID brandId);

}