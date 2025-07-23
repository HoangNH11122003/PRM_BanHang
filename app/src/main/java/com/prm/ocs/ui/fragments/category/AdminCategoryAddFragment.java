package com.prm.ocs.ui.fragments.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.prm.ocs.R;
import com.prm.ocs.controller.CategoryController;
import com.prm.ocs.data.db.entity.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminCategoryAddFragment extends Fragment {
    private EditText nameEditText, descriptionEditText;
    private Button saveButton;
    private CategoryController categoryController;


    private static final String ARG_CATEGORY_ID = "category_id";

    public static AdminCategoryAddFragment newInstance(UUID categoryId) {
        AdminCategoryAddFragment fragment = new AdminCategoryAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_category_detail, container, false);

        nameEditText = view.findViewById(R.id.category_name);
        descriptionEditText = view.findViewById(R.id.category_description);
        saveButton = view.findViewById(R.id.save_button);
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        categoryController = new CategoryController(getContext());

        saveButton.setOnClickListener(v -> addCategory());

        return view;
    }

    private void addCategory() {
        try {
            Category newCategory = new Category();
            newCategory.setCategoryId(UUID.fromString(getArguments().getString(ARG_CATEGORY_ID)));
            newCategory.setName(nameEditText.getText().toString());
            newCategory.setDescription(descriptionEditText.getText().toString());

            if (newCategory.getName().isEmpty()) {
                Toast.makeText(getContext(), "Category name is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newCategory.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Category description is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            categoryController.addCategory(newCategory);
            Toast.makeText(getContext(), "Category added", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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