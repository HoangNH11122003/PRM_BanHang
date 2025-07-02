package com.prm.ocs.ui.fragments.brand;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prm.ocs.R;
import com.prm.ocs.controller.BrandController;
import com.prm.ocs.data.dto.BrandProductsDto;
import com.prm.ocs.ui.adapters.BrandListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminBrandListFragment extends Fragment implements BrandController.BrandListCallback {
    private RecyclerView recyclerView;
    private BrandListAdapter adapter;
    private BrandController brandController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_brand_list, container, false);

        recyclerView = view.findViewById(R.id.brand_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BrandListAdapter(getContext());
        recyclerView.setAdapter(adapter);

        brandController = new BrandController(getContext());
        brandController.loadBrandsWithProductCount(this); // Sử dụng hàm trả DTO

        return view;
    }

    @Override
    public void onBrandsLoaded(List<BrandProductsDto> brands) {
        adapter.setBrands(brands);
    }

    //onResume
    @Override
    public void onResume() {
        super.onResume();
        brandController.loadBrandsWithProductCount(this);
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