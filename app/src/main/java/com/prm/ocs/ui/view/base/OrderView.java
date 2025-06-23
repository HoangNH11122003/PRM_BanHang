package com.prm.ocs.ui.view.base;

import com.prm.ocs.data.db.entity.Order;
import com.prm.ocs.data.db.entity.OrderDetail;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderView {
    void displayOrderHistory(List<Order> orders, Map<String, Map<UUID, String>> productInfo);
    void displayOrderDetails(Order order, List<OrderDetail> orderDetails, Map<UUID, String> productNames);
}