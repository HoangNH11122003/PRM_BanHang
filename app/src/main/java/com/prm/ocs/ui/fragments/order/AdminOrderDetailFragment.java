package com.prm.ocs.ui.fragments.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.controller.OrderController;
import com.prm.ocs.controller.OrderDetailController;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.db.entity.Order;
import com.prm.ocs.data.db.entity.OrderDetail;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.data.dto.OrderDetailWithProductName;
import com.prm.ocs.ui.adapters.AdminOrderDetailAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AdminOrderDetailFragment extends Fragment implements OrderController.OrderDetailCallback, OrderDetailController.OrderDetailsCallback {
    private static final String ARG_ORDER_ID = "order_id";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private EditText orderIdEditText, userIdEditText, totalAmountEditText, addressEditText;
    private TextView orderDateTextView;
    private Spinner statusSpinner;
    private RecyclerView productsRecyclerView;
    private AdminOrderDetailAdapter adapter;
    private Button saveButton, deleteButton;
    private OrderController orderController;
    private OrderDetailController orderDetailController;
    private ProductController productController; // Thêm ProductController
    private Order currentOrder;

    public static AdminOrderDetailFragment newInstance(UUID orderId) {
        AdminOrderDetailFragment fragment = new AdminOrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order_detail, container, false);

        orderIdEditText = view.findViewById(R.id.order_id);
        userIdEditText = view.findViewById(R.id.user_id);
        orderDateTextView = view.findViewById(R.id.order_date);
        totalAmountEditText = view.findViewById(R.id.total_amount);
        addressEditText = view.findViewById(R.id.address);
        statusSpinner = view.findViewById(R.id.status);
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);

        orderController = new OrderController(getContext());
        orderDetailController = new OrderDetailController(getContext());
        productController = new ProductController(getContext());

        // Thiết lập RecyclerView
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminOrderDetailAdapter(new ArrayList<>());
        productsRecyclerView.setAdapter(adapter);

        // Thiết lập Spinner cho status
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.order_statuses,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // Tải dữ liệu Order và Order Details
        UUID orderId = UUID.fromString(getArguments().getString(ARG_ORDER_ID));
        orderController.loadOrderDetails(orderId, this);
        orderDetailController.loadOrderDetails(orderId, this);

        saveButton.setOnClickListener(v -> saveOrder());
        deleteButton.setOnClickListener(v -> deleteOrder());

        return view;
    }

    @Override
    public void onOrderLoaded(Order order) {
        currentOrder = order;
        orderIdEditText.setText(order.getOrderId().toString());
        userIdEditText.setText(order.getUserId().toString());
        orderDateTextView.setText(dateFormat.format(order.getOrderDate()));
        totalAmountEditText.setText(String.format(Locale.getDefault(), "%.2f", order.getTotalAmount()));
        addressEditText.setText(order.getAddress());

        // Thiết lập giá trị cho Spinner
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) statusSpinner.getAdapter();
        int position = adapter.getPosition(order.getStatus());
        statusSpinner.setSelection(position);
    }

    @Override
    public void onOrderDetailsLoaded(List<OrderDetail> orderDetails) {
        // Truy vấn tên sản phẩm từ cơ sở dữ liệu
        DatabaseClient dbClient = DatabaseClient.getInstance(getContext());
        List<OrderDetailWithProductName> orderDetailsWithNames = new ArrayList<>();

        dbClient.getExecutorService().execute(() -> {
            for (OrderDetail detail : orderDetails) {
                Product product = dbClient.getAppDatabase().productDao().getProductById(detail.getProductId());
                String productName = (product != null && product.getName() != null) ? product.getName() : "Unknown";
                orderDetailsWithNames.add(new OrderDetailWithProductName(detail, productName));
            }

            // Cập nhật adapter trên luồng chính
            requireActivity().runOnUiThread(() -> {
                adapter.setOrderDetails(orderDetailsWithNames);
            });
        });
    }


}