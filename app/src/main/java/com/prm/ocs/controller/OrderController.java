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

    public void loadOrders(OrderCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final List<Order> orders = dbClient.getAppDatabase().orderDao().getAllOrders();
            dbClient.getMainHandler().post(() -> callback.onOrdersLoaded(orders));
        });
    }

    public void loadOrderDetails(UUID orderId, OrderDetailCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final Order order = dbClient.getAppDatabase().orderDao().getOrderById(orderId);
            dbClient.getMainHandler().post(() -> callback.onOrderLoaded(order));
        });
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

    public void loadOrderHistory(UUID userId) {
        dbClient.getExecutorService().execute(() -> {
            // Lấy danh sách đơn hàng
            final List<Order> orders = dbClient.getAppDatabase().orderDao().getOrdersByUser(userId);
            if (orders == null || orders.isEmpty()) {
                dbClient.getMainHandler().post(() -> view.displayOrderHistory(orders, new HashMap<>()));
                return;
            }

            // Lấy danh sách order details cho tất cả đơn hàng
            Map<UUID, String> productNames = new HashMap<>();
            Map<UUID, String> productImages = new HashMap<>();
            for (Order order : orders) {
                List<OrderDetail> orderDetails = dbClient.getAppDatabase().orderDetailDao().getOrderDetailsByOrderId(order.getOrderId());
                if (orderDetails != null && !orderDetails.isEmpty()) {
                    // Lấy thông tin sản phẩm từ order detail đầu tiên
                    OrderDetail firstDetail = orderDetails.get(0);
                    Product product = dbClient.getAppDatabase().productDao().getProductById(firstDetail.getProductId());
                    if (product != null) {
                        productNames.put(order.getOrderId(), product.getName());
                        productImages.put(order.getOrderId(), product.getImage());
                    }
                }
            }

            // Truyền dữ liệu về view
            Map<String, Map<UUID, String>> productInfo = new HashMap<>();
            productInfo.put("productNames", productNames);
            productInfo.put("productImages", productImages);

            dbClient.getMainHandler().post(() -> view.displayOrderHistory(orders, productInfo));
        });
    }

    //Count the number of orders
    public void getOrderCount(CountCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            try {
                final int count = dbClient.getAppDatabase().orderDao().getOrderCount();
                dbClient.getMainHandler().post(() -> callback.onCountLoaded(count));
            } catch (Exception e) {
                dbClient.getMainHandler().post(() -> callback.onError("Error getting order count: " + e.getMessage()));
            }
        });
    }

    // New method to calculate total amount for ALL orders
    public void calculateTotalAmountForAllOrders(TotalRevenueCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            try {
                Double totalRevenue = dbClient.getAppDatabase().orderDetailDao().getTotalRevenue();
                if (totalRevenue == null || totalRevenue.isNaN()) {
                    totalRevenue = 0.0;
                }
                double finalTotal = totalRevenue;

                dbClient.getMainHandler().post(() -> callback.onTotalAmountCalculated(finalTotal));
            } catch (Exception e) {
                dbClient.getMainHandler().post(() -> callback.onError("Error calculating total revenue: " + e.getMessage()));
            }
        });
    }

    public void addOrder(Order order) {
        dbClient.getExecutorService().execute(() -> dbClient.getAppDatabase().orderDao().insert(order));
    }

    public void updateOrder(Order order) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().orderDao().update(order);
        });
    }

    //Catch exception if the order is used in any product
    public void deleteOrder(Order order) {
        dbClient.getExecutorService().execute(() -> {
            try {
                dbClient.getAppDatabase().orderDao().delete(order);
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText((Context) view, "Order '" + order.getOrderId() + "' deleted successfully.", Toast.LENGTH_SHORT).show();
                });
            } catch (SQLiteConstraintException e) {
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText((Context) view, "Cannot delete order '" + order.getOrderId() + "'. Order details are still associated with it.", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText((Context) view, "Error deleting order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Tính số lượng sản phẩm trong Order trên luồng nền
    public void calculateProductCount(UUID orderId, ProductCountCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final int count = dbClient.getAppDatabase().orderDetailDao().getOrderDetailsByOrderId(orderId).size();
            dbClient.getMainHandler().post(() -> callback.onProductCountCalculated(count));
        });
    }

    // Tính tổng số tiền của Order trên luồng nền
    public void calculateTotalAmount(UUID orderId, TotalAmountCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            double totalAmount = 0;
            List<OrderDetail> orderDetails = dbClient.getAppDatabase().orderDetailDao().getOrderDetailsByOrderId(orderId);
            for (OrderDetail orderDetail : orderDetails) {
                totalAmount += orderDetail.getPrice() * orderDetail.getQuantity();
            }
            double finalTotalAmount = totalAmount;
            dbClient.getMainHandler().post(() -> callback.onTotalAmountCalculated(finalTotalAmount));
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