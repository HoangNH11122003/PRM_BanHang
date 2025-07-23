package com.prm.ocs.controller;

import android.content.Context;

import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.db.entity.OrderDetail;

import java.util.List;
import java.util.UUID;

public class OrderDetailController {
    private DatabaseClient dbClient;

    public OrderDetailController(Context context) {
        this.dbClient = DatabaseClient.getInstance(context);
    }

    public void loadOrderDetails(UUID orderId, OrderDetailsCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final List<OrderDetail> orderDetails = dbClient.getAppDatabase().orderDetailDao().getOrderDetailsByOrderId(orderId);
            dbClient.getMainHandler().post(() -> callback.onOrderDetailsLoaded(orderDetails));
        });
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        dbClient.getExecutorService().execute(() -> dbClient.getAppDatabase().orderDetailDao().insert(orderDetail));
    }

    public void addOrderDetails(List<OrderDetail> orderDetails) {
        dbClient.getExecutorService().execute(() -> {
            for (OrderDetail detail : orderDetails) {
                dbClient.getAppDatabase().orderDetailDao().insert(detail);
            }
        });
    }

    public void updateOrderDetail(OrderDetail orderDetail) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().orderDetailDao().update(orderDetail);
        });
    }

    public void deleteOrderDetail(OrderDetail orderDetail) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().orderDetailDao().delete(orderDetail);
        });
    }

    public interface OrderDetailsCallback {
        void onOrderDetailsLoaded(List<OrderDetail> orderDetails);
    }
    public void dummyFunction1() {
        // Đây là một hàm mẫu không làm gì cả
    }

    public int dummyFunction2(int a, int b) {
        // Hàm mẫu trả về một giá trị bất kỳ, không có tác dụng thực tế
        return a + b;
    }

    public void dummyFunction3(String message) {
        // Hàm mẫu chỉ in ra một thông báo
        System.out.println(message);
    }

    public String dummyFunction4() {
        // Hàm mẫu trả về một chuỗi bất kỳ
        return "Hello, world!";
    }

}