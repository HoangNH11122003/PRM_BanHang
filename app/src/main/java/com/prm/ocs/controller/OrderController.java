package com.prm.ocs.controller;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;

import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.db.entity.Order;
import com.prm.ocs.data.db.entity.OrderDetail;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.ui.view.base.OrderView;
import com.prm.ocs.utils.CountCallback;
import com.prm.ocs.utils.TotalRevenueCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderController {
    private final DatabaseClient dbClient;

    private final OrderView view;

    public OrderController(Context context) {
        this.dbClient = DatabaseClient.getInstance(context);
        this.view = null; // view không cần thiết trong trường hợp này
    }

    public OrderController(Context context, OrderView view) {
        this.dbClient = DatabaseClient.getInstance(context);
        this.view = view;
    }




    public void loadOrderDetails(UUID orderId) {
        dbClient.getExecutorService().execute(() -> {
            final Order order = dbClient.getAppDatabase().orderDao().getOrderById(orderId);
            final List<OrderDetail> orderDetails = dbClient.getAppDatabase().orderDetailDao().getOrderDetailsByOrderId(orderId);

            // Lấy thông tin sản phẩm cho từng order detail
            Map<UUID, String> productNames = new HashMap<>();
            if (orderDetails != null) {
                for (OrderDetail detail : orderDetails) {
                    Product product = dbClient.getAppDatabase().productDao().getProductById(detail.getProductId());
                    if (product != null) {
                        productNames.put(detail.getOrderDetailId(), product.getName());
                    }
                }
            }

            final Map<UUID, String> finalProductNames = productNames;
            dbClient.getMainHandler().post(() -> {
                if (order == null || orderDetails == null) {
                    if (view instanceof Context) {
                        Toast.makeText((Context) view, "Failed to load order details", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                view.displayOrderDetails(order, orderDetails, finalProductNames);
            });
        });
    }


    public interface OrderCallback {
        void onOrdersLoaded(List<Order> orders);
    }

    public interface OrderDetailCallback {
        void onOrderLoaded(Order order);
    }

    public interface ProductCountCallback {
        void onProductCountCalculated(int count);
    }

    public interface TotalAmountCallback {
        void onTotalAmountCalculated(double totalAmount);
    }
}