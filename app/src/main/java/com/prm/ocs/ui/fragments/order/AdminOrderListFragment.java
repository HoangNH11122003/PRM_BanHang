package com.prm.ocs.ui.fragments.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView; // Import đúng
import androidx.core.util.Pair; // Import Pair
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.datepicker.MaterialDatePicker; // Import DatePicker
import com.prm.ocs.R;
import com.prm.ocs.controller.OrderController;
import com.prm.ocs.data.db.entity.Order;
import com.prm.ocs.data.dto.OrderSummaryDto;
import com.prm.ocs.ui.adapters.AdminOrderListAdapter;
import com.prm.ocs.ui.view.order.AdminOrderListActivity; // Giữ lại nếu cần tham chiếu Activity

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors; // Sử dụng Stream API cho dễ lọc

public class AdminOrderListFragment extends Fragment implements OrderController.OrderCallback {
    private static final String TAG = "AdminOrderListFragment";

    // Views
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private Button dateRangePickerButton;
    private TextView selectedDateRangeText;
    private ImageButton clearFiltersButton;
    private LinearLayout emptyViewContainer;
    private TextView emptyViewText;

    // Data & Controller
    private AdminOrderListAdapter adapter;
    private OrderController orderController;
    private List<OrderSummaryDto> fullOrderList = new ArrayList<>(); // Danh sách gốc
    private List<OrderSummaryDto> displayedOrderList = new ArrayList<>(); // Danh sách hiển thị

    // Filter State
    private String currentTextQuery = "";
    private Long currentStartDate = null; // Lưu dạng Milliseconds UTC
    private Long currentEndDate = null;   // Lưu dạng Milliseconds UTC

    // Helpers
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private AtomicInteger processedOrdersCounter; // Cho việc load product count

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order_list, container, false);

        findViews(view);
        setupSearchView();
        setupClearButton();
        setupSwipeToRefresh(); // Setup listener refresh

        orderController = new OrderController(getContext());

        // Load data lần đầu khi resume (sẽ gọi trong onResume)
        // loadOrderData();

        return view;
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.order_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        searchView = view.findViewById(R.id.search_view_order);
        dateRangePickerButton = view.findViewById(R.id.button_date_range_picker);
        selectedDateRangeText = view.findViewById(R.id.text_selected_date_range);
        clearFiltersButton = view.findViewById(R.id.button_clear_filters);
        emptyViewContainer = view.findViewById(R.id.empty_view_container);
        emptyViewText = view.findViewById(R.id.empty_view_text);
    }



    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Không cần xử lý submit riêng nếu muốn lọc ngay khi gõ
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentTextQuery = newText != null ? newText.trim() : "";
                applyFilters(); // Lọc lại danh sách khi text thay đổi
                return true;
            }
        });
    }



    // Điều chỉnh endDate về cuối ngày (23:59:59.999) theo Local Timezone


    private void setupClearButton() {
        clearFiltersButton.setOnClickListener(v -> resetFiltersAndLoad(true)); // true = gọi load lại data
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Swipe to refresh: Resetting filters and loading data.");
            resetFiltersAndLoad(true); // Reset và tải lại
        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.purple_500,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Hàm reset bộ lọc
    private void resetFiltersAndLoad(boolean reloadData) {
        Log.d(TAG, "Resetting filters...");
        currentTextQuery = "";
        currentStartDate = null;
        currentEndDate = null;

        // Reset UI
        searchView.setQuery("", false); // Xóa text trong SearchView
        searchView.clearFocus(); // Bỏ focus khỏi searchview
        selectedDateRangeText.setText("Tất cả ngày");

        if (reloadData) {
            loadOrderData(); // Tải lại toàn bộ dữ liệu


        }
    }


    // Hàm tải dữ liệu (giữ nguyên như trước)
    private void loadOrderData() {
        Log.d(TAG, "Loading all orders...");
        if (!swipeRefreshLayout.isRefreshing()) { // Chỉ hiển thị khi không phải do swipe
            // Có thể hiển thị ProgressBar riêng ở đây nếu muốn
        }
        orderController.loadOrders(this); // Callback sẽ là onOrdersLoaded
    }



    // Hàm áp dụng bộ lọc hiện tại lên fullOrderList và cập nhật adapter


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed. Loading initial data or refreshing.");
        // Tải dữ liệu khi quay lại màn hình.
        // KHÔNG reset filter ở đây, chỉ tải lại và áp dụng filter cũ (nếu có)
        loadOrderData();
    }

    // Thêm các callback lỗi nếu có từ OrderController
    // public void onError(String message) { ... }
}