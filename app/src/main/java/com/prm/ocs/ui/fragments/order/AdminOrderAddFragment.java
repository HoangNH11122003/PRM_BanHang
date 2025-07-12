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

        return view;
    }



    private void addOrderDetailRow() {
        View orderDetailRow = LayoutInflater.from(getContext()).inflate(R.layout.item_order_detail_input, null);
        ImageButton removeButton = orderDetailRow.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(v -> orderDetailsContainer.removeView(orderDetailRow));
        orderDetailsContainer.addView(orderDetailRow);
    }




}