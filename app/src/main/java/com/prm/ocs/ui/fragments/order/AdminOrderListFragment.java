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
        setupRecyclerView();
        setupSearchView();
        setupDateRangePicker();
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

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Adapter khởi tạo với danh sách sẽ hiển thị (ban đầu rỗng)
        adapter = new AdminOrderListAdapter(getContext(), displayedOrderList, order -> {
            if (getActivity() instanceof AdminOrderListActivity) { // Hoặc interface callback ra Activity
                ((AdminOrderListActivity) getActivity()).showOrderDetail(order.getOrder().getOrderId());
            }
        });
        recyclerView.setAdapter(adapter);
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

    private void setupDateRangePicker() {
        dateRangePickerButton.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Chọn khoảng ngày");

            // Set giá trị mặc định nếu đã chọn trước đó
            if (currentStartDate != null && currentEndDate != null) {
                builder.setSelection(new Pair<>(currentStartDate, currentEndDate));
            }

            MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

            picker.addOnPositiveButtonClickListener(selection -> {

                currentStartDate = selection.first;
                currentEndDate = adjustEndDate(selection.second);

                // Hiển thị ngày đã chọn
                String startDateStr = displayDateFormat.format(new Date(currentStartDate));
                String endDateStr = displayDateFormat.format(new Date(selection.second)); // Hiển thị ngày người dùng chọn
                selectedDateRangeText.setText(String.format("%s - %s", startDateStr, endDateStr));
                selectedDateRangeText.setVisibility(View.VISIBLE);

                applyFilters(); // Lọc lại danh sách
            });

            picker.addOnNegativeButtonClickListener(dialog -> {

            });
            picker.addOnCancelListener(dialog -> {

            });


            picker.show(getParentFragmentManager(), picker.toString());
        });
    }

    // Điều chỉnh endDate về cuối ngày (23:59:59.999) theo Local Timezone
    private Long adjustEndDate(Long utcMidnightMillis) {
        if (utcMidnightMillis == null) return null;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault()); // Dùng Timezone mặc định của máy
        calendar.setTimeInMillis(utcMidnightMillis);
        // Chuyển về đầu ngày theo local timezone (đề phòng picker trả về ko chuẩn)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // Cộng thêm 1 ngày và trừ đi 1 mili giây để lấy thời điểm cuối ngày
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTimeInMillis();
    }


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
        } else {
            applyFilters(); // Chỉ áp dụng lại bộ lọc (trống) lên dữ liệu hiện có
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

    @Override
    public void onOrdersLoaded(List<Order> orders) {
        Log.d(TAG, "Loaded " + (orders != null ? orders.size() : 0) + " base orders.");
        if (getView() == null) return;

        // Ẩn biểu tượng loading nếu đang refresh
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        // Xử lý lấy product count (logic này cần xem lại nếu quá chậm)
        // Tạm thời giữ nguyên logic cũ để tập trung vào search
        fullOrderList.clear(); // Xóa danh sách gốc cũ
        if (orders == null || orders.isEmpty()) {
            Log.d(TAG, "Order list is empty or null.");
            applyFilters(); // Áp dụng bộ lọc (sẽ hiển thị empty view)
            return;
        }

        processedOrdersCounter = new AtomicInteger(0);
        final int totalOrdersToProcess = orders.size();
        List<OrderSummaryDto> tempSummaries = new ArrayList<>(); // List tạm để build

        for (Order order : orders) {
            final Order currentOrder = order;
            orderController.calculateProductCount(currentOrder.getOrderId(), new OrderController.ProductCountCallback() {
                @Override
                public void onProductCountCalculated(int productCount) {
                    double totalAmount = currentOrder.getTotalAmount();
                    OrderSummaryDto summaryDto = new OrderSummaryDto(currentOrder, productCount, totalAmount);
                    tempSummaries.add(summaryDto);

                    int currentProcessedCount = processedOrdersCounter.incrementAndGet();
                    if (currentProcessedCount == totalOrdersToProcess) {
                        Log.d(TAG, "Finished processing all product counts.");
                        // --- Quan trọng: Cập nhật danh sách gốc và áp dụng bộ lọc ---
                        fullOrderList.clear();
                        fullOrderList.addAll(tempSummaries); // Lưu vào danh sách gốc
                        applyFilters(); // Áp dụng bộ lọc hiện tại lên danh sách mới tải
                    }
                }
                // Thêm onError cho calculateProductCount nếu có
            });
        }
    }

    // Hàm áp dụng bộ lọc hiện tại lên fullOrderList và cập nhật adapter
    private void applyFilters() {
        Log.d(TAG, "Applying filters: Text='" + currentTextQuery + "', Start=" + currentStartDate + ", End=" + currentEndDate);
        if (getView() == null) return;

        List<OrderSummaryDto> filteredList;

        // Lọc bằng Stream API cho gọn
        filteredList = fullOrderList.stream()
                .filter(summary -> {
                    Order order = summary.getOrder();
                    if (order == null || order.getOrderId() == null) return false; // Add null check for orderId

                    // 1. Lọc theo Text (Order ID hoặc User ID)
                    boolean textMatch = true; // Default true if query is empty
                    if (!currentTextQuery.isEmpty()) {
                        String lowerQuery = currentTextQuery.toLowerCase();

                        // *** START: Modified Text Match Logic ***
                        if (lowerQuery.length() == 32) {
                            // Special case: Assume 32-char query is a UUID without hyphens
                            String orderIdNoHyphen = order.getOrderId().toString().replace("-", "").toLowerCase();
                            String userIdNoHyphen = (order.getUserId() != null) ? order.getUserId().toString().replace("-", "").toLowerCase() : "";

                            // Exact match against UUIDs (without hyphens)
                            textMatch = orderIdNoHyphen.equals(lowerQuery) || (!userIdNoHyphen.isEmpty() && userIdNoHyphen.equals(lowerQuery));
                        } else {
                            // Original logic: Check if query is contained within full UUID strings
                            String orderIdStr = order.getOrderId().toString().toLowerCase();
                            // Handle potential null UserId safely
                            String userIdStr = (order.getUserId() != null) ? order.getUserId().toString().toLowerCase() : "";
                            textMatch = orderIdStr.contains(lowerQuery) || (!userIdStr.isEmpty() && userIdStr.contains(lowerQuery));
                        }
                        // *** END: Modified Text Match Logic ***
                    }

                    // 2. Lọc theo Ngày
                    boolean dateMatch = true; // Mặc định là true nếu không chọn ngày
                    if (currentStartDate != null && currentEndDate != null) {
                        if (order.getOrderDate() == null) return false; // Add null check for date
                        long orderTime = order.getOrderDate().getTime();
                        dateMatch = orderTime >= currentStartDate && orderTime <= currentEndDate;
                    } else if (currentStartDate != null) {
                        if (order.getOrderDate() == null) return false; // Add null check for date
                        dateMatch = order.getOrderDate().getTime() >= currentStartDate;
                    } else if (currentEndDate != null) {
                        if (order.getOrderDate() == null) return false; // Add null check for date
                        long orderTime = order.getOrderDate().getTime();
                        dateMatch = orderTime <= currentEndDate;
                    }

                    // Kết hợp AND
                    return textMatch && dateMatch;
                })
                .collect(Collectors.toList());

        Log.d(TAG, "Filtering complete. Found " + filteredList.size() + " matching orders.");

        // Cập nhật Adapter (Logic giữ nguyên)
        displayedOrderList.clear();
        displayedOrderList.addAll(filteredList);
        adapter.notifyDataSetChanged();

        // Cập nhật Empty View (Logic giữ nguyên)
        if (displayedOrderList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyViewContainer.setVisibility(View.VISIBLE);
            if (!currentTextQuery.isEmpty() || currentStartDate != null) {
                emptyViewText.setText("Không tìm thấy đơn hàng phù hợp.");
            } else {
                emptyViewText.setText("Chưa có đơn hàng nào.");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewContainer.setVisibility(View.GONE);
        }
    }

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