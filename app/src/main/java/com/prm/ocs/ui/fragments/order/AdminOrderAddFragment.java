package com.prm.ocs.ui.fragments.order;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.prm.ocs.R;
import com.prm.ocs.controller.OrderController;
import com.prm.ocs.controller.OrderDetailController;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.data.db.entity.Order;
import com.prm.ocs.data.db.entity.OrderDetail;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.utils.UUIDUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AdminOrderAddFragment extends Fragment {
    private EditText userIdEditText, addressEditText;
    private TextView orderDateTextView;
    private Spinner statusSpinner;
    private Button saveButton;
    private LinearLayout orderDetailsContainer;
    private OrderController orderController;
    private OrderDetailController orderDetailController;
    private ProductController productController;
    private List<OrderDetail> orderDetailsList = new ArrayList<>();
    private double totalAmount = 0.0;
    private int processedItems = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final String ARG_ORDER_ID = "order_id";

    public static AdminOrderAddFragment newInstance(UUID orderId) {
        AdminOrderAddFragment fragment = new AdminOrderAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order_add, container, false);

        userIdEditText = view.findViewById(R.id.user_id);
        orderDateTextView = view.findViewById(R.id.order_date);
        addressEditText = view.findViewById(R.id.address);
        statusSpinner = view.findViewById(R.id.status);
        saveButton = view.findViewById(R.id.save_button);
        orderDetailsContainer = view.findViewById(R.id.order_details_container);
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        orderController = new OrderController(getContext());
        orderDetailController = new OrderDetailController(getContext());
        productController = new ProductController(getContext());

        // Thiết lập DatePicker cho order_date
        orderDateTextView.setOnClickListener(v -> showDatePickerDialog());

        // Thiết lập Spinner cho status
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.order_statuses,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // Thêm một hàng Order Detail mặc định
        addOrderDetailRow();

        // Nút để thêm Order Detail mới
        ImageButton addOrderDetailButton = view.findViewById(R.id.add_order_detail_button);
        addOrderDetailButton.setOnClickListener(v -> addOrderDetailRow());

        saveButton.setOnClickListener(v -> prepareOrder());

        return view;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    orderDateTextView.setText(dateFormat.format(selectedDate.getTime()));
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void addOrderDetailRow() {
        View orderDetailRow = LayoutInflater.from(getContext()).inflate(R.layout.item_order_detail_input, null);
        ImageButton removeButton = orderDetailRow.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(v -> orderDetailsContainer.removeView(orderDetailRow));
        orderDetailsContainer.addView(orderDetailRow);
    }

    private void prepareOrder() {
        try {
            // Tạo Order mới
            Order newOrder = new Order();
            newOrder.setOrderId(UUID.fromString(getArguments().getString(ARG_ORDER_ID)));
            UUID userId = UUIDUtils.parseUUID(userIdEditText.getText().toString());
            if (userId == null) {
                Toast.makeText(getContext(), "Invalid User ID format", Toast.LENGTH_SHORT).show();
                return;
            }
            newOrder.setUserId(userId);

            // Parse Order Date
            Date orderDate = dateFormat.parse(orderDateTextView.getText().toString());
            newOrder.setOrderDate(orderDate);

            newOrder.setAddress(addressEditText.getText().toString());
            newOrder.setStatus(statusSpinner.getSelectedItem().toString());

            // Chuẩn bị Order Details và tính Total Amount
            orderDetailsList.clear();
            totalAmount = 0.0;
            processedItems = 0;

            for (int i = 0; i < orderDetailsContainer.getChildCount(); i++) {
                View row = orderDetailsContainer.getChildAt(i);

                EditText productIdEditText = row.findViewById(R.id.product_id_input);
                // Parse UUID from String input like "123e4567e89b12d3a456426614174000"
                UUID productId = UUIDUtils.parseUUID(productIdEditText.getText().toString());
                if (productId == null) {
                    Toast.makeText(getContext(), "Invalid Product ID format at row " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }

                EditText quantityEditText = row.findViewById(R.id.quantity_input);

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityEditText.getText().toString());
                    if (quantity <= 0) {
                        Toast.makeText(getContext(), "Quantity must be greater than 0 at row " + (i + 1), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid quantity format at row " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(newOrder.getOrderId());
                orderDetail.setProductId(productId);
                orderDetail.setQuantity(quantity);

                // Truy vấn giá sản phẩm trên luồng nền
                productController.getProductById(productId, new ProductController.ProductCallback() {
                    @Override
                    public void onProductLoaded(Product product) {
                        int leftProducts = (product != null) ? product.getStock() : 0;

                        if (quantity > leftProducts) {
                            Toast.makeText(getContext(), "Not enough products in stock for product " + productId, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double unitPrice = (product != null) ? product.getSellingPrice() : 0.0;
                        orderDetail.setPrice(unitPrice);
                        totalAmount += unitPrice * quantity;
                        orderDetailsList.add(orderDetail);

                        // Kiểm tra xem đã xử lý hết các sản phẩm chưa
                        processedItems++;
                        if (processedItems == orderDetailsContainer.getChildCount()) {
                            // Đã xử lý hết, lưu Order
                            saveOrder(newOrder);
                        }
                    }
                });
            }
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid date format. Use dd/MM/yyyy", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOrder(Order newOrder) {
        newOrder.setTotalAmount(totalAmount);
        orderController.addOrder(newOrder);
        orderDetailController.addOrderDetails(orderDetailsList);
        Toast.makeText(getContext(), "Order added", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }
}