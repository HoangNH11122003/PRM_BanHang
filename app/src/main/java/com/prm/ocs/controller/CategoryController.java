package com.prm.ocs.controller;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.widget.Toast;

import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.db.entity.Category;
import com.prm.ocs.data.dto.CategoryProductsDto;
import com.prm.ocs.utils.CountCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryController {
    private final Context context;
    private final DatabaseClient dbClient;

    public CategoryController(Context context) {
        this.dbClient = DatabaseClient.getInstance(context);
        this.context = context;
    }

    // Trả về DTO cho danh sách có số lượng sản phẩm
    public void loadCategoriesWithProductCount(CategoryListCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final List<Category> categories = dbClient.getAppDatabase().categoryDao().getAllCategories();
            List<CategoryProductsDto> categoryDtos = new ArrayList<>();
            for (Category category : categories) {
                int productCount = dbClient.getAppDatabase().categoryDao().getProductCountByCategory(category.getCategoryId());
                categoryDtos.add(new CategoryProductsDto(category, productCount));
            }
            dbClient.getMainHandler().post(() -> callback.onCategoriesLoaded(categoryDtos));
        });
    }

    // Trả về model thường cho các trường hợp khác
    public void loadCategories(CategorySimpleCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final List<Category> categories = dbClient.getAppDatabase().categoryDao().getAllCategories();
            dbClient.getMainHandler().post(() -> callback.onSimpleCategoriesLoaded(categories));
        });
    }

    public void loadCategoryDetails(UUID categoryId, CategoryDetailCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final Category category = dbClient.getAppDatabase().categoryDao().getCategoryById(categoryId);
            dbClient.getMainHandler().post(() -> callback.onCategoryLoaded(category));
        });
    }

    //Count the number of categories
    public void getCategoryCount(CountCallback callback) {
        try {
            dbClient.getExecutorService().execute(() -> {
                int count = dbClient.getAppDatabase().categoryDao().getCategoryCount();
                dbClient.getMainHandler().post(() -> callback.onCountLoaded(count));
            });
        } catch (Exception e) {
            dbClient.getMainHandler().post(() -> callback.onError("Error getting order count: " + e.getMessage()));
        }

    }

    public void addCategory(Category category) {
        dbClient.getExecutorService().execute(() -> dbClient.getAppDatabase().categoryDao().insert(category));
    }

    public void updateCategory(Category category) {
        dbClient.getExecutorService().execute(() -> dbClient.getAppDatabase().categoryDao().update(category));
    }

    //Catch exception if the category is used in any product
    public void deleteCategory(Category category) {
        dbClient.getExecutorService().execute(() -> {
            try {
                dbClient.getAppDatabase().categoryDao().delete(category);
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(context, "Category '" + category.getName() + "' deleted successfully.", Toast.LENGTH_SHORT).show();
                });
            } catch (SQLiteConstraintException e) {
                Log.w("CategoryController", "Constraint error deleting category: " + category.getCategoryId(), e);
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(context, "Cannot delete category '" + category.getName() + "'. Products are still associated with it.", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                Log.e("CategoryController", "Error deleting category: " + category.getCategoryId(), e);
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(context, "Error deleting category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Callback cho DTO
    public interface CategoryListCallback {
        void onCategoriesLoaded(List<CategoryProductsDto> categories);
    }

    // Callback cho model thường
    public interface CategorySimpleCallback {
        void onSimpleCategoriesLoaded(List<Category> categories);
    }

    public interface CategoryDetailCallback {
        void onCategoryLoaded(Category category);
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