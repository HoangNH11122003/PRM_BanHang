package com.prm.ocs.ui.fragments.brand;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.controller.BrandController;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.data.db.entity.Brand;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.ui.adapters.AdminProductListAdapter;
import com.prm.ocs.ui.fragments.product.AdminProductDetailFragment;
import com.prm.ocs.ui.view.base.ProductView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminBrandDetailFragment extends Fragment implements BrandController.BrandDetailCallback, ProductView, AdminProductListAdapter.OnProductClickListener {
    private static final String ARG_BRAND_ID = "brand_id";
    private EditText nameEditText, descriptionEditText;
    private Button saveButton, deleteButton;
    private RecyclerView productsRecyclerView;
    private AdminProductListAdapter adminProductListAdapter;
    private BrandController brandController;
    private ProductController productController;
    private Brand currentBrand;

    public static AdminBrandDetailFragment newInstance(UUID brandId) {
        AdminBrandDetailFragment fragment = new AdminBrandDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BRAND_ID, brandId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_brand_detail, container, false);

        nameEditText = view.findViewById(R.id.brand_name);
        descriptionEditText = view.findViewById(R.id.brand_description);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);

        brandController = new BrandController(getContext());
        productController = new ProductController(this);

        // Khởi tạo RecyclerView với ProductAdapter
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adminProductListAdapter = new AdminProductListAdapter(getContext(), this);
        productsRecyclerView.setAdapter(adminProductListAdapter);

        UUID brandId = UUID.fromString(getArguments().getString(ARG_BRAND_ID));
        brandController.loadBrandDetails(brandId, this);
        productController.loadProductsByBrand(brandId);

        saveButton.setOnClickListener(v -> updateBrand());
        deleteButton.setOnClickListener(v -> deleteBrand());

        return view;
    }

    @Override
    public void onBrandLoaded(Brand brand) {
        currentBrand = brand;
        if (brand != null) {
            nameEditText.setText(brand.getName());
            descriptionEditText.setText(brand.getDescription());
        }
    }

    @Override
    public void displayProducts(List<Product> products) {
        adminProductListAdapter.setProducts(products);
    }

    @Override
    public void displayProductDetails(Product product) {
        // Không dùng trong Fragment này
    }

    @Override
    public void onProductClick(UUID productId) {
        // Xử lý khi nhấn nút "More" - ví dụ: mở AdminProductDetailFragment
        AdminProductDetailFragment fragment = AdminProductDetailFragment.newInstance(productId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateBrand() {
        try {
            if (currentBrand == null) return;

            currentBrand.setName(nameEditText.getText().toString());
            currentBrand.setDescription(descriptionEditText.getText().toString());
            brandController.updateBrand(currentBrand);

            //validate
            if (currentBrand.getName().isEmpty()) {
                Toast.makeText(getContext(), "Brand name is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentBrand.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Brand description is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Brand saved", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBrand() {
        if (currentBrand == null) return;
        brandController.deleteBrand(currentBrand);
        getParentFragmentManager().popBackStack();
    }
    // === BEGIN PLACEHOLDER METHODS ===

    public void markOrderAsSeen(UUID orderId) {
        System.out.println("markOrderAsSeen called for order: " + orderId);
    }

    public void sendOrderConfirmation(UUID orderId) {
        System.out.println("sendOrderConfirmation called for order: " + orderId);
    }

    public boolean validateOrderData(Object order) {
        return true;
    }

    public void logOrderStatus(UUID orderId, String status) {
        System.out.println("Logging order " + orderId + " with status: " + status);
    }

    public boolean isOrderEmpty(UUID orderId) {
        return false;
    }

    public void simulateNetworkDelay() {
        try {
            Thread.sleep(100); // giả lập delay
        } catch (InterruptedException ignored) {}
    }

    public void fakeUploadProgress(UUID fileId) {
        for (int i = 0; i <= 100; i += 10) {
            System.out.println("Upload progress for " + fileId + ": " + i + "%");
        }
    }

    public void backupNothing() {
        System.out.println("Backup started...");
        System.out.println("No data found. Backup skipped.");
    }

    public void printDebugLog() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Debug point " + i);
        }
    }

    public boolean pretendToAuthenticate(String username, String password) {
        return true; // luôn đúng
    }

    public void generateFakeReport() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Generating report section " + i);
        }
    }

    public void waitForNothing() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}
    }

    public String meaninglessTransformation(String input) {
        return input.toUpperCase().toLowerCase().trim();
    }

    public void simulateDataSync() {
        System.out.println("Starting fake data sync...");
        System.out.println("Finished syncing 0 records.");
    }

    public void dummyCacheClear() {
        System.out.println("Cache clear executed. No cache found.");
    }

    public boolean fakePermissionCheck(String role) {
        return true;
    }

    public void testPlaceholder(UUID id, String name) {
        System.out.println("Testing placeholder with ID: " + id + ", Name: " + name);
    }

    public int randomZero() {
        return 0;
    }

    public void pretendSaveAction() {
        System.out.println("Pretend saving to database... Done.");
    }

    public boolean checkIfCoolFeatureIsOn() {
        return false; // chưa bật
    }

    public void logFakeException() {
        System.out.println("Simulated exception caught: NullPointerException (not real)");
    }

    public void uselessLoop() {
        for (int i = 0; i < 100; i++) {
            int a = i * 2;
            a /= 2;
        }
    }

    public void simulateTimeout() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}
    }

    public List<String> getDummyList() {
        List<String> list = new ArrayList<>();
        list.add("Alpha");
        list.add("Beta");
        list.add("Gamma");
        return list;
    }

    public void placeholderPrintList(List<?> items) {
        for (Object item : items) {
            System.out.println("Item: " + item);
        }
    }

    public void fakeNotificationTrigger() {
        System.out.println("Fake notification sent to 0 users.");
    }

    public boolean isEnvironmentReady() {
        return true;
    }

    public String echo(String input) {
        return input;
    }

    public void noOperation() {
        // intentionally left blank
    }

    public void placeholderMethod123() {
        System.out.println("Placeholder 123 activated.");
    }

    public boolean mockValidationCheck() {
        return true;
    }

    public void incrementNothing() {
        int counter = 0;
        counter++;
        counter--;
    }

// === END PLACEHOLDER METHODS ===
}