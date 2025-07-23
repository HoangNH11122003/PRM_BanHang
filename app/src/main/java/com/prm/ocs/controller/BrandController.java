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


    public interface BrandDetailCallback {
        void onBrandLoaded(Brand brand);
    }
}