package com.prm.ocs.ui.view.order;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.controller.OrderController;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.data.db.entity.Order;
import com.prm.ocs.data.db.entity.OrderDetail;
import com.prm.ocs.ui.adapters.OrderAdapter;
import com.prm.ocs.ui.adapters.OrderDetailAdapter;
import com.prm.ocs.ui.manager.SessionManager;
import com.prm.ocs.ui.view.base.OrderView;
import com.prm.ocs.utils.UUIDUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class OrderHistoryActivity extends AppCompatActivity implements OrderView, OrderAdapter.OnOrderClickListener {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private OrderController controller;
    private UserController userController;
    private ProgressBar progressBar;
    private View emptyStateLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerView = findViewById(R.id.order_history_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, this);
        recyclerView.setAdapter(adapter);
        toolbar = findViewById(R.id.toolbar);

        controller = new OrderController(this, this);
        userController = new UserController(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút Back
            getSupportActionBar().setTitle("Order History"); // Đảm bảo tiêu đề khớp với XML
        }

        // Xử lý sự kiện nhấn nút Back
        toolbar.setNavigationOnClickListener(v -> {
            finish(); // Quay lại activity trước đó
        });

        UUIDUtils uuidUtils = new UUIDUtils();
        UUID userId = UUIDUtils.parseUUID("E746B3CC8F1A4E82B2B9EADB4C93768C");

        SessionManager sessionManager = new SessionManager(this);
        userId = UUID.fromString(sessionManager.getUserId());
        controller.loadOrderHistory(userId);
    }

    @Override
    public void displayOrderHistory(List<Order> orders, Map<String, Map<UUID, String>> productInfo) {
        progressBar.setVisibility(View.GONE);
        if (orders == null || orders.isEmpty()) {
            Log.d("OrderHistory", "No orders found for this user");
            adapter.setOrders(new ArrayList<>(), productInfo);
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            adapter.setOrders(orders, productInfo);
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Log.d("OrderHistory", "Loaded " + orders.size() + " orders");
        }
    }

    @Override
    public void displayOrderDetails(Order order, List<OrderDetail> orderDetails, Map<UUID, String> productNames) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order #" + order.getOrderId().toString().substring(0, 8));

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_order_details, null);
        TextView orderIdText = dialogView.findViewById(R.id.dialog_order_id);
        TextView orderDateText = dialogView.findViewById(R.id.dialog_order_date);
        TextView totalAmountText = dialogView.findViewById(R.id.dialog_order_total);
        RecyclerView detailsRecyclerView = dialogView.findViewById(R.id.order_details_recycler_view);

        // Định dạng và hiển thị thông tin đơn hàng
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        orderIdText.setText("Order #" + order.getOrderId().toString().substring(0, 8));
        orderDateText.setText("Date: " + sdf.format(order.getOrderDate()));
        totalAmountText.setText("Total: $" + String.format(Locale.getDefault(), "%.2f", order.getTotalAmount()));

        // Thiết lập RecyclerView để hiển thị chi tiết đơn hàng
        detailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        OrderDetailAdapter detailAdapter = new OrderDetailAdapter();
        detailAdapter.setOrderDetails(orderDetails, productNames);
        detailsRecyclerView.setAdapter(detailAdapter);

        builder.setView(dialogView);

        // Tùy chỉnh nút "OK"
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

        // Tùy chỉnh giao diện nút "OK"
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundResource(android.R.color.transparent);
    }

    @Override
    public void onOrderClick(Order order) {
        Log.d("OrderHistory", "Clicked on order: " + order.getOrderId());
        controller.loadOrderDetails(order.getOrderId());
    }
}