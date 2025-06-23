package com.prm.ocs.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import com.prm.ocs.data.db.entity.OrderDetail;

import java.util.List;
import java.util.UUID;

@Dao
public interface OrderDetailDao extends BaseDao<OrderDetail>{

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> getOrderDetailsByOrderId(UUID orderId);

    @Query("SELECT * FROM order_details")
    List<OrderDetail> getAllOrderDetails();

    @Query("SELECT * FROM order_details WHERE orderDetailId = :orderDetailId")
    OrderDetail getOrderDetailById(UUID orderDetailId);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> getOrderDetailsByOrder(UUID orderId);

    @Query("Select Sum(quantity*price) FROM order_details")
    double getTotalRevenue();

}