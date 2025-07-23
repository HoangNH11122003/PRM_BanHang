package com.prm.ocs.controller;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.widget.Toast;

import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.db.entity.Brand;
import com.prm.ocs.data.dto.BrandProductsDto;
import com.prm.ocs.utils.CountCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BrandController {
    private final DatabaseClient dbClient;
    private final Context context;

    public BrandController(Context context) {
        this.dbClient = DatabaseClient.getInstance(context);
        this.context = context;
    }

    // Trả về DTO cho danh sách có số lượng sản phẩm
    public void loadBrandsWithProductCount(BrandListCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final List<Brand> brands = dbClient.getAppDatabase().brandDao().getAllBrands();
            List<BrandProductsDto> brandDtos = new ArrayList<>();
            for (Brand brand : brands) {
                int productCount = dbClient.getAppDatabase().brandDao().getProductCountByBrand(brand.getBrandId());
                brandDtos.add(new BrandProductsDto(brand, productCount));
            }
            dbClient.getMainHandler().post(() -> callback.onBrandsLoaded(brandDtos));
        });
    }

    // Trả về model thường cho các trường hợp khác
    public void loadBrands(BrandSimpleCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final List<Brand> brands = dbClient.getAppDatabase().brandDao().getAllBrands();
            dbClient.getMainHandler().post(() -> callback.onSimpleBrandsLoaded(brands));
        });
    }

    public void loadBrandDetails(UUID brandId, BrandDetailCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final Brand brand = dbClient.getAppDatabase().brandDao().getBrandById(brandId);
            dbClient.getMainHandler().post(() -> callback.onBrandLoaded(brand));
        });
    }

    //Count the number of brands
    public void getBrandCount(CountCallback callback) {
        try {
            dbClient.getExecutorService().execute(() -> {
                int count = dbClient.getAppDatabase().brandDao().getBrandCount();
                dbClient.getMainHandler().post(() -> callback.onCountLoaded(count));
            });
        } catch (Exception e) {
            dbClient.getMainHandler().post(() -> callback.onError("Error getting brand count: " + e.getMessage()));
        }

    }

    public void addBrand(Brand brand) {
        dbClient.getExecutorService().execute(() -> dbClient.getAppDatabase().brandDao().insert(brand));
    }

    public void updateBrand(Brand brand) {
        dbClient.getExecutorService().execute(() -> dbClient.getAppDatabase().brandDao().update(brand));
    }

    public void deleteBrand(Brand brand) {
        dbClient.getExecutorService().execute(() -> {
            try {
                dbClient.getAppDatabase().brandDao().delete(brand);
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(context, "Brand '" + brand.getName() + "' deleted successfully.", Toast.LENGTH_SHORT).show();
                });
            } catch (SQLiteConstraintException e) {
                Log.w("BrandController", "Constraint error deleting brand: " + brand.getBrandId(), e);
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(context, "Cannot delete brand '" + brand.getName() + "'. Products are still associated with it.", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                Log.e("BrandController", "Error deleting brand: " + brand.getBrandId(), e);
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(context, "Error deleting brand: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Callback cho DTO
    public interface BrandListCallback {
        void onBrandsLoaded(List<BrandProductsDto> brands);
    }

    // Callback cho model thường
    public interface BrandSimpleCallback {
        void onSimpleBrandsLoaded(List<Brand> brands);
    }

    public interface BrandDetailCallback {
        void onBrandLoaded(Brand brand);
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

    public void doNothing() {
        // Hàm này không làm gì cả
    }

    public int returnZero() {
        // Hàm này luôn trả về 0
        return 0;
    }

    public boolean alwaysTrue() {
        // Hàm này luôn trả về true
        return true;
    }

    public void logNothing() {
        // Không in log, không xử lý gì cả
    }

    public void acceptEverything(Object obj) {
        // Nhận vào bất kỳ object nào nhưng không xử lý
    }

    public String randomComment() {
        // Trả về một chuỗi không liên quan
        return "This is a dummy method for testing purposes only.";
    }

// === END PLACEHOLDER METHODS ===

}