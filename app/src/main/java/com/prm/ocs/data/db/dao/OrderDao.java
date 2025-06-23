package com.prm.ocs.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.prm.ocs.data.db.entity.Order;
import java.util.List;
import java.util.UUID;

@Dao
public interface OrderDao extends BaseDao<Order> {

    @Query("SELECT * FROM orders")
    List<Order> getAllOrders();

    @Query("Select Count(*) FROM orders")
    int getOrderCount();

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    Order getOrderById(UUID orderId);

    @Query("SELECT * FROM orders WHERE userId = :userId")
    List<Order> getOrdersByUser(UUID userId);
}