package com.prm.ocs.controller;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.ui.view.base.ProductView;
import com.prm.ocs.utils.CountCallback;

import java.util.List;
import java.util.UUID;

public class ProductController {
    private final ProductView view;
    private final Context _context;
    private final DatabaseClient dbClient;

    // Constructor cho trường hợp có ProductView
    public ProductController(ProductView view) {
        this.view = view;
        Context context = getContextFromView(view);
        _context = context;
        this.dbClient = DatabaseClient.getInstance(context);
    }

    // Constructor cho trường hợp không cần ProductView
    public ProductController(Context context) {
        this.view = null;
        _context = context;
        this.dbClient = DatabaseClient.getInstance(context);
    }

    // Helper method để lấy Context từ ProductView
    private Context getContextFromView(ProductView view) {
        if (view instanceof Fragment) {
            return ((Fragment) view).requireContext();
        } else if (view instanceof Context) {
            return (Context) view;
        } else {
            throw new IllegalArgumentException("ProductView must be a Fragment or Context");
        }
    }

    public void loadProducts() {
        dbClient.getExecutorService().execute(() -> {
            final List<Product> products = dbClient.getAppDatabase().productDao().getAllProducts();

            dbClient.getMainHandler().post(() -> {
                view.displayProducts(products);
            });
        });
    }

    public void loadProductsByCategory(String categoryId) {
        dbClient.getExecutorService().execute(() -> {
            UUID categoryUUID = UUID.fromString(categoryId); // Convert categoryId to UUID
            final List<Product> products = dbClient.getAppDatabase().productDao().getProductsByCategory(categoryUUID);
            dbClient.getMainHandler().post(() -> {
                view.displayProducts(products);
            });
        });
    }

    public void loadProductsByBrand(String brandId) {
        dbClient.getExecutorService().execute(() -> {
            UUID brandUUID = UUID.fromString(brandId); // Convert brandId to UUID//
            final List<Product> products = dbClient.getAppDatabase().productDao().getProductsByBrand(brandUUID);
            dbClient.getMainHandler().post(() -> {
                view.displayProducts(products);
            });
        });
    }

    public void loadProductDetails(UUID productId) {
        if (view == null) {
            throw new IllegalStateException("ProductView is required for this operation");
        }
        dbClient.getExecutorService().execute(() -> {
            final Product product = dbClient.getAppDatabase().productDao().getProductById(productId);
            dbClient.getMainHandler().post(() -> view.displayProductDetails(product));
        });
    }

    public void loadProductsByBrand(UUID brandId) {
        if (view == null) {
            throw new IllegalStateException("ProductView is required for this operation");
        }
        dbClient.getExecutorService().execute(() -> {
            final List<Product> products = dbClient.getAppDatabase().productDao().getProductsByBrand(brandId);
            dbClient.getMainHandler().post(() -> view.displayProducts(products));
        });
    }

    public void loadProductsByCategory(UUID categoryId) {
        if (view == null) {
            throw new IllegalStateException("ProductView is required for this operation");
        }
        dbClient.getExecutorService().execute(() -> {
            final List<Product> products = dbClient.getAppDatabase().productDao().getProductsByCategory(categoryId);
            dbClient.getMainHandler().post(() -> view.displayProducts(products));
        });
    }

    public void addProduct(Product product) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().productDao().insert(product);
            dbClient.getMainHandler().post(() -> {
                // Có thể thêm thông báo hoặc cập nhật giao diện nếu cần
            });
        });
    }

    public void updateProduct(Product product) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().productDao().update(product);
            dbClient.getMainHandler().post(() -> {
                // Có thể thêm thông báo hoặc cập nhật giao diện nếu cần
            });
        });
    }

    //Catch exception if the product is used in any order as below demo code

    public void deleteProduct(Product product) {
        dbClient.getExecutorService().execute(() -> {
            try {
                dbClient.getAppDatabase().productDao().delete(product);
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(_context, "Product '" + product.getName() + "' deleted successfully.", Toast.LENGTH_SHORT).show();
                });
            } catch (SQLiteConstraintException e) {
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(_context, "Cannot delete product '" + product.getName() + "'. Orders are still associated with it.", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(_context, "Error deleting product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void getProductById(UUID productId, ProductCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final Product product = dbClient.getAppDatabase().productDao().getProductById(productId);
            dbClient.getMainHandler().post(() -> callback.onProductLoaded(product));
        });
    }

    public void getProductCount(CountCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final int count = dbClient.getAppDatabase().productDao().getProductCount();
            dbClient.getMainHandler().post(() -> callback.onCountLoaded(count));
        });
    }

    public interface ProductCallback {
        void onProductLoaded(Product product);
    }
    public void dummyFunction1() {
        // Đây là một hàm mẫu không làm gì cả
    }

    public int dummyFunction2(int a, int b) {
        // Hàm mẫu trả về một giá trị bất kỳ, không có tác dụng thực tế
        return a + b;
    }

    public void dummyFunction3(String message) {
        // Hàm mẫu chỉ in ra một thông báo
        System.out.println(message);
    }

    public String dummyFunction4() {

        return "Hello, world!";
    }

    public void doAbsolutelyNothing() {
        // Không có nội dung gì
    }


    public void acceptInputButIgnoreEverything(int x, String y) {
        // Nhận dữ liệu nhưng không xử lý
    }


    public Product returnNullProduct() {
        return null;
    }


}