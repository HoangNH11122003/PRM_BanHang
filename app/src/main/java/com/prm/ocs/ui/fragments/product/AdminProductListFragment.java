package com.prm.ocs.ui.fragments.product;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.prm.ocs.R;

// Import các Controller và Callback
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.controller.CategoryController;
import com.prm.ocs.controller.BrandController;

import com.prm.ocs.data.db.entity.Product;
// --- Sử dụng Entity thật thay vì model giả định ---
import com.prm.ocs.data.db.entity.Category;
import com.prm.ocs.data.db.entity.Brand;
// --- ---

import com.prm.ocs.ui.adapters.AdminProductListAdapter;
import com.prm.ocs.ui.view.base.ProductView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

// Implement thêm các Callback interfaces
public class AdminProductListFragment extends Fragment implements
        ProductView,
        AdminProductListAdapter.OnProductClickListener,
        CategoryController.CategorySimpleCallback, // Thêm Callback cho Category
        BrandController.BrandSimpleCallback      // Thêm Callback cho Brand
{
    private static final String TAG = "AdminProductListFrag";

    // Views (giữ nguyên)
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private Spinner spinnerCategory;
    private Spinner spinnerBrand;
    private Button buttonPriceRange;
    private TextView textSelectedPriceRange;
    private SwitchMaterial switchStock;
    private ImageButton buttonClearFilters;
    private LinearLayout emptyViewContainer;
    private TextView emptyViewText;
    private FloatingActionButton fabScrollToTop;

    // Adapters (giữ nguyên)
    private AdminProductListAdapter adminProductListAdapter;
    private ArrayAdapter<String> categorySpinnerAdapter;
    private ArrayAdapter<String> brandSpinnerAdapter;

    // Controllers
    private ProductController productController;
    private CategoryController categoryController; // Thêm CategoryController
    private BrandController brandController;       // Thêm BrandController

    // Data (giữ nguyên)
    private List<Product> fullProductList = new ArrayList<>();
    private List<Product> displayedProductList = new ArrayList<>();
    private List<Category> allCategories = new ArrayList<>();
    private List<Brand> allBrands = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    // Filter State (giữ nguyên)
    private String currentQuery = "";
    private Category selectedCategory = null;
    private Brand selectedBrand = null;
    private Float currentMinPrice = null;
    private Float currentMaxPrice = null;
    private boolean isStockRequired = false;

    // Helpers (giữ nguyên)
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    private final float MAX_PRICE_SLIDER = 500f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product_list, container, false);

        findViews(view);
        setupRecyclerView();
        setupSearchView();
        setupSpinners(); // Setup cấu trúc spinner trước khi load data
        setupPriceRangeButton();
        setupStockSwitch();
        setupClearButton();
        setupFab();
        setupSwipeToRefresh();

        // Khởi tạo Controllers
        productController = new ProductController(this);
        categoryController = new CategoryController(requireContext()); // Khởi tạo CategoryController
        brandController = new BrandController(requireContext());       // Khởi tạo BrandController

        loadAllData(); // <<< Gọi hàm load data thật

        return view;
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_clothes);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_product);
        searchView = view.findViewById(R.id.search_view_product);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerBrand = view.findViewById(R.id.spinner_brand);
        buttonPriceRange = view.findViewById(R.id.button_price_range);
        textSelectedPriceRange = view.findViewById(R.id.text_selected_price_range);
        switchStock = view.findViewById(R.id.switch_stock);
        buttonClearFilters = view.findViewById(R.id.button_clear_filters);
        emptyViewContainer = view.findViewById(R.id.empty_view_container_product);
        emptyViewText = view.findViewById(R.id.empty_view_text_product);
        fabScrollToTop = view.findViewById(R.id.fab_scroll_to_top);
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adminProductListAdapter = new AdminProductListAdapter(requireContext(), this);
        recyclerView.setAdapter(adminProductListAdapter);
    }

    private void setupFab() {
        fabScrollToTop.setOnClickListener(v -> {
            recyclerView.smoothScrollToPosition(0); // Hoặc cuộn mượt
            //recyclerView.scrollToPosition(0);
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                currentQuery = newText != null ? newText.trim() : "";
                applyFilters();
                return true;
            }
        });
    }

    private void setupSpinners() {
        // --- Category Spinner ---
        // Chỉ khởi tạo adapter, data sẽ được load sau
        categorySpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categorySpinnerAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // "Tất cả loại"
                    selectedCategory = null;
                } else {
                    if (!allCategories.isEmpty() && position - 1 < allCategories.size()) {
                        // Lấy category thật từ list allCategories dựa vào vị trí đã trừ đi 1 (do có item "Tất cả")
                        selectedCategory = allCategories.get(position - 1);
                    } else {
                        selectedCategory = null; // Safety check
                        Log.w(TAG, "Category selection error: position " + position + " out of bounds or list empty.");
                    }
                }
                Log.d(TAG, "Category selected: " + (selectedCategory != null ? selectedCategory.getName() : "All"));
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { selectedCategory = null; applyFilters();}
        });

        // --- Brand Spinner ---
        // Chỉ khởi tạo adapter, data sẽ được load sau
        brandSpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        brandSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(brandSpinnerAdapter);
        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // "Tất cả hãng"
                    selectedBrand = null;
                } else {
                    if (!allBrands.isEmpty() && position - 1 < allBrands.size()) {
                        // Lấy brand thật từ list allBrands
                        selectedBrand = allBrands.get(position - 1);
                    } else {
                        selectedBrand = null; // Safety check
                        Log.w(TAG, "Brand selection error: position " + position + " out of bounds or list empty.");
                    }
                }
                Log.d(TAG, "Brand selected: " + (selectedBrand != null ? selectedBrand.getName() : "All"));
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { selectedBrand = null; applyFilters(); }
        });
    }


    private void setupPriceRangeButton() {
        buttonPriceRange.setOnClickListener(v -> showPriceRangeDialog());
    }

    private void setupStockSwitch() {
        switchStock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isStockRequired = isChecked;
            applyFilters();
        });
    }

    private void setupClearButton() {
        buttonClearFilters.setOnClickListener(v -> resetFiltersAndLoad(false));
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Swipe to refresh triggered.");
            resetFiltersAndLoad(true);
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.purple_500, android.R.color.holo_green_light);
    }

    // --- Loading Data ---

    private void loadAllData() {
        Log.d(TAG, "Loading all data (products, categories, brands)...");
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true); // Hiển thị loading
        }
        // Gọi các hàm load từ controller
        productController.loadProducts();
        categoryController.loadCategories(this); // Truyền "this" vì Fragment implement callback
        brandController.loadBrands(this);       // Truyền "this"
    }

    @Override
    public void displayProducts(List<Product> products) {
        Log.d(TAG, "Callback: Loaded " + (products != null ? products.size() : 0) + " products.");
        if (getView() == null) return;

        // Chỉ tắt loading khi product load xong (có thể cải thiện sau)
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        fullProductList.clear();
        if (products != null) {
            fullProductList.addAll(products);
        }
        applyFilters(); // Áp dụng bộ lọc sau khi có danh sách sản phẩm đầy đủ
    }

    @Override
    public void onSimpleCategoriesLoaded(List<Category> categories) {
        Log.d(TAG, "Callback: Loaded " + (categories != null ? categories.size() : 0) + " categories.");
        if (getView() == null) return;

        allCategories.clear();
        if (categories != null) {
            allCategories.addAll(categories);
        }

        // Cập nhật Category Spinner Adapter
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Tất cả loại"); // Item mặc định
        for (Category cat : allCategories) {
            categoryNames.add(cat.getName());
        }
        // Cập nhật adapter trên Main Thread (mặc dù controller đã post lên Main Thread, nhưng để chắc chắn)
        requireActivity().runOnUiThread(() -> {
            categorySpinnerAdapter.clear();
            categorySpinnerAdapter.addAll(categoryNames);
            categorySpinnerAdapter.notifyDataSetChanged();
            // Reset selection nếu cần (ví dụ: khi đang chọn category A, refresh, list category thay đổi)
            if (selectedCategory == null) {
                spinnerCategory.setSelection(0, false);
            } else {
                // Tìm vị trí của category cũ trong list mới
                int oldIndex = -1;
                for (int i = 0; i < allCategories.size(); i++) {
                    if (allCategories.get(i).getCategoryId().equals(selectedCategory.getCategoryId())) {
                        oldIndex = i;
                        break;
                    }
                }
                spinnerCategory.setSelection(oldIndex != -1 ? oldIndex + 1 : 0, false); // +1 vì có "Tất cả"
            }
        });
    }

    @Override
    public void onSimpleBrandsLoaded(List<Brand> brands) {
        Log.d(TAG, "Callback: Loaded " + (brands != null ? brands.size() : 0) + " brands.");
        if (getView() == null) return;

        allBrands.clear();
        if (brands != null) {
            allBrands.addAll(brands);
        }

        // Cập nhật Brand Spinner Adapter
        List<String> brandNames = new ArrayList<>();
        brandNames.add("Tất cả hãng"); // Item mặc định
        for (Brand brand : allBrands) {
            brandNames.add(brand.getName());
        }
        // Cập nhật adapter trên Main Thread
        requireActivity().runOnUiThread(() -> {
            brandSpinnerAdapter.clear();
            brandSpinnerAdapter.addAll(brandNames);
            brandSpinnerAdapter.notifyDataSetChanged();
            // Reset selection nếu cần
            if (selectedBrand == null) {
                spinnerBrand.setSelection(0, false);
            } else {
                int oldIndex = -1;
                for (int i = 0; i < allBrands.size(); i++) {
                    if (allBrands.get(i).getBrandId().equals(selectedBrand.getBrandId())) {
                        oldIndex = i;
                        break;
                    }
                }
                spinnerBrand.setSelection(oldIndex != -1 ? oldIndex + 1 : 0, false); // +1 vì có "Tất cả"
            }
        });
    }

    @Override
    public void displayProductDetails(Product product) {
        // Không dùng ở màn hình list
        Log.w(TAG, "displayProductDetails called unexpectedly in list fragment.");
    }

    // --- Filtering Logic (applyFilters) giữ nguyên ---
    private void applyFilters() {
        Log.d(TAG, "Applying filters: Query='" + currentQuery
                + "', Category=" + (selectedCategory != null ? selectedCategory.getName() : "All")
                + "', Brand=" + (selectedBrand != null ? selectedBrand.getName() : "All")
                + "', MinPrice=" + currentMinPrice
                + "', MaxPrice=" + currentMaxPrice
                + "', StockRequired=" + isStockRequired);

        if (getView() == null || fullProductList == null) {
            Log.w(TAG, "applyFilters cancelled: view or fullProductList is null.");
            return;
        }

        List<Product> filteredList = fullProductList.stream()
                .filter(product -> {
                    if (product == null) return false;

                    // 1. Filter by Name (text query)
                    boolean nameMatch = currentQuery.isEmpty() || (product.getName() != null &&
                            product.getName().toLowerCase().contains(currentQuery.toLowerCase()));

                    // 2. Filter by Category
                    boolean categoryMatch = selectedCategory == null ||
                            (product.getCategoryId() != null &&
                                    product.getCategoryId().equals(selectedCategory.getCategoryId())); // So sánh UUID

                    // 3. Filter by Brand
                    boolean brandMatch = selectedBrand == null ||
                            (product.getBrandId() != null &&
                                    product.getBrandId().equals(selectedBrand.getBrandId())); // So sánh UUID

                    // 4. Filter by Price Range
                    boolean priceMatch = true; // Giả sử phù hợp ban đầu
                    if (currentMinPrice != null && product.getSellingPrice() < currentMinPrice) {
                        priceMatch = false;
                    }
                    if (priceMatch && currentMaxPrice != null && product.getSellingPrice() > currentMaxPrice) {
                        priceMatch = false;
                    }

                    // 5. Filter by Stock
                    boolean stockMatch = !isStockRequired || product.getStock() > 0;

                    return nameMatch && categoryMatch && brandMatch && priceMatch && stockMatch;
                })
                .collect(Collectors.toList());

        Log.d(TAG, "Filtering complete. Displaying " + filteredList.size() + " products.");

        // Cập nhật RecyclerView trên Main Thread
        requireActivity().runOnUiThread(() -> {
            if (adminProductListAdapter != null) { // Kiểm tra null đề phòng
                Log.d(TAG, "UI Thread: Calling setProducts with " + filteredList.size() + " items.");
                adminProductListAdapter.setProducts(filteredList); // <-- Gọi setProducts
                Log.d(TAG, "UI Thread: After setProducts. Adapter count: " + adminProductListAdapter.getItemCount());
                updateEmptyView(filteredList.isEmpty());
                Log.d(TAG, "UI Thread: After updateEmptyView.");
            } else {
                Log.e(TAG, "UI Thread: adminProductListAdapter is null in applyFilters!");
            }
        });
    }


    // --- resetFiltersAndLoad (Cập nhật để gọi loadAllData) ---
    private void resetFiltersAndLoad(boolean reloadData) {
        Log.d(TAG, "Resetting filters. Reload data: " + reloadData);
        // Reset state
        currentQuery = "";
        selectedCategory = null;
        selectedBrand = null;
        currentMinPrice = null;
        currentMaxPrice = null;
        isStockRequired = false;

        // Reset UI
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
        }
        if (spinnerCategory != null) spinnerCategory.setSelection(0, false);
        if (spinnerBrand != null) spinnerBrand.setSelection(0, false);
        if (textSelectedPriceRange != null) textSelectedPriceRange.setText("Tất cả giá");
        if (switchStock != null) switchStock.setChecked(false);

        if (reloadData) {
            loadAllData(); // <<< Gọi loadAllData thay vì chỉ load product
        } else {
            applyFilters();
        }
    }

    // --- showPriceRangeDialog giữ nguyên ---
    private void showPriceRangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_price_range, null);
        builder.setView(dialogView);
        builder.setTitle("Chọn Khoảng Giá");

        RangeSlider rangeSlider = dialogView.findViewById(R.id.range_slider_price);
        TextView currentRangeText = dialogView.findViewById(R.id.text_current_range);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        Button buttonApply = dialogView.findViewById(R.id.button_apply);

        // Thiết lập slider
        rangeSlider.setValueFrom(0f);
        rangeSlider.setValueTo(MAX_PRICE_SLIDER); // Giá trị max
        rangeSlider.setStepSize(25f); // Bước nhảy 10k

        // Đặt giá trị hiện tại cho slider
        float currentMin = (currentMinPrice != null) ? currentMinPrice : 0f;
        float currentMax = (currentMaxPrice != null && currentMaxPrice <= MAX_PRICE_SLIDER) ? currentMaxPrice : MAX_PRICE_SLIDER;
        // Đảm bảo min <= max khi set vào slider
        if (currentMin > currentMax) {
            currentMin = currentMax;
        }
        rangeSlider.setValues(currentMin, currentMax);
        currentRangeText.setText(String.format("Giá: %s - %s",
                currencyFormatter.format(currentMin), currencyFormatter.format(currentMax)));

        rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            // Đảm bảo giá trị hiển thị luôn min <= max
            currentRangeText.setText(String.format("Giá: %s - %s",
                    currencyFormatter.format(Math.min(values.get(0), values.get(1))),
                    currencyFormatter.format(Math.max(values.get(0), values.get(1)))));
        });

        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        buttonApply.setOnClickListener(v -> {
            List<Float> values = rangeSlider.getValues();
            // Lấy giá trị min và max thực sự từ slider
            float appliedMin = Math.min(values.get(0), values.get(1));
            float appliedMax = Math.max(values.get(0), values.get(1));

            // Cập nhật state
            currentMinPrice = (appliedMin <= 0f) ? null : appliedMin; // Coi 0 là không lọc min
            currentMaxPrice = (appliedMax >= MAX_PRICE_SLIDER) ? null : appliedMax; // Coi max là không lọc max

            // Cập nhật TextView hiển thị ngoài dialog
            if (currentMinPrice == null && currentMaxPrice == null) {
                textSelectedPriceRange.setText("Tất cả giá");
            } else if (currentMinPrice != null && currentMaxPrice != null) {
                textSelectedPriceRange.setText(String.format("%s - %s",
                        currencyFormatter.format(currentMinPrice), currencyFormatter.format(currentMaxPrice)));
            } else if (currentMinPrice != null) {
                textSelectedPriceRange.setText(String.format("Từ %s", currencyFormatter.format(currentMinPrice)));
            } else { // Chỉ có currentMaxPrice
                textSelectedPriceRange.setText(String.format("Đến %s", currencyFormatter.format(currentMaxPrice)));
            }

            applyFilters();
            dialog.dismiss();
        });

        dialog.show();
    }

    // --- updateEmptyView giữ nguyên ---
    private void updateEmptyView(boolean show) {
        if (getView() == null) return;
        if (show) {
            recyclerView.setVisibility(View.GONE);
            emptyViewContainer.setVisibility(View.VISIBLE);
            boolean hasActiveFilter = !currentQuery.isEmpty() || selectedCategory != null || selectedBrand != null || currentMinPrice != null || currentMaxPrice != null || isStockRequired;
            if (hasActiveFilter) {
                emptyViewText.setText("Không tìm thấy sản phẩm phù hợp.");
            } else {
                emptyViewText.setText("Chưa có sản phẩm nào.");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewContainer.setVisibility(View.GONE);
        }
    }

    // --- Lifecycle & Navigation ---
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed. Loading all data.");
        // Gọi loadAllData thay vì chỉ load products khi resume
        loadAllData();
    }

    @Override
    public void onProductClick(UUID productId) {
        Log.d(TAG, "Product clicked: " + productId);
        AdminProductDetailFragment fragment = AdminProductDetailFragment.newInstance(productId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // ID của container trong Activity
                .addToBackStack(null)
                .commit();
    }
}