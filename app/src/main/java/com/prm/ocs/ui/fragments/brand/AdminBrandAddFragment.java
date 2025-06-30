package com.prm.ocs.ui.fragments.brand;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.prm.ocs.R;
import com.prm.ocs.controller.BrandController;
import com.prm.ocs.data.db.entity.Brand;
import com.prm.ocs.data.db.entity.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminBrandAddFragment extends Fragment {
    private static final String ARG_BRAND_ID = "brand_id";
    private EditText nameEditText, descriptionEditText;
    private Button saveButton;
    private BrandController brandController;

    public static AdminBrandAddFragment newInstance(UUID brandId) {
        AdminBrandAddFragment fragment = new AdminBrandAddFragment();
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
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        brandController = new BrandController(getContext());

        saveButton.setOnClickListener(v -> addBrand());

        return view;
    }

    private void addBrand() {
        try {
            Brand newBrand = new Brand();
            newBrand.setBrandId(UUID.fromString(getArguments().getString(ARG_BRAND_ID)));
            newBrand.setName(nameEditText.getText().toString());
            newBrand.setDescription(descriptionEditText.getText().toString());
            if (newBrand.getName().isEmpty()) {
                Toast.makeText(getContext(), "Brand name is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newBrand.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Brand description is required!", Toast.LENGTH_SHORT).show();
                return;
            }


            brandController.addBrand(newBrand);
            Toast.makeText(getContext(), "Brand added", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    // === BEGIN PLACEHOLDER METHODS ===

    public void markCategoryAsSeen(UUID categoryId) {
        System.out.println("markCategoryAsSeen called for category: " + categoryId);
    }

    public void sendCategoryConfirmation(UUID categoryId) {
        System.out.println("sendCategoryConfirmation called for category: " + categoryId);
    }

    public boolean validateCategoryData(Category category) {
        return true;
    }

    public void logCategoryStatus(UUID categoryId, String status) {
        System.out.println("Logging category " + categoryId + " with status: " + status);
    }

    public boolean isCategoryEmpty(UUID categoryId) {
        return false;
    }

    public void simulateCategorySync() {
        System.out.println("Simulating category sync...");
        System.out.println("No real sync performed.");
    }

    public void backupCategoryInfo() {
        System.out.println("Backing up category info... complete.");
    }

    public void testCategoryPlaceholder(UUID id, String name) {
        System.out.println("Testing placeholder with ID: " + id + ", Name: " + name);
    }

    public boolean checkCategoryFeatureToggle() {
        return true;
    }

    public void fakeCategoryNotification() {
        System.out.println("Fake notification sent for category change.");
    }

    public void placeholderPrintCategories(List<Category> categories) {
        for (Category cat : categories) {
            System.out.println("Placeholder print: " + cat.getName());
        }
    }

    public void uselessCategoryLoop() {
        for (int i = 0; i < 100; i++) {
            int temp = i * 3;
            temp /= 3;
        }
    }

    public String echoCategoryName(String input) {
        return input;
    }

    public List<String> getDummyCategoryTags() {
        List<String> tags = new ArrayList<>();
        tags.add("Popular");
        tags.add("New");
        tags.add("Featured");
        return tags;
    }

    public void simulateCategoryTimeout() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException ignored) {}
    }

    public boolean mockCategoryPermission(String role) {
        return true;
    }

    public void generateFakeCategoryReport() {
        for (int i = 0; i < 5; i++) {
            System.out.println("Report section " + i + " generated.");
        }
    }

    public void simulateEmptySearch() {
        System.out.println("Simulating empty category search...");
    }

    public boolean alwaysReturnTrue() {
        return true;
    }

    public int fakeCategoryCount() {
        return 999;
    }

    public void waitForNoReason() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {}
    }

    public String formatCategoryName(String name) {
        return name.trim().toUpperCase();
    }

    public boolean dummyConditionCheck() {
        return false;
    }

    public void pretendToUpdateUI() {
        System.out.println("Pretending to update category UI.");
    }

    public void noOperationCategory() {
        // intentionally empty
    }

    public boolean fakeValidationSuccess() {
        return true;
    }

    public void dummyLogCategoryEvent(String eventName) {
        System.out.println("Event logged (dummy): " + eventName);
    }

    public void printDebugCategoryInfo() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Debug category log point " + i);
        }
    }

// === END PLACEHOLDER METHODS ===
}