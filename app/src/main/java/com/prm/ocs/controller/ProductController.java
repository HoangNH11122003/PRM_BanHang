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
            UUID brandUUID = UUID.fromString(brandId); // Convert brandId to UUID
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

    public void dummyFunction5() {
        // Hàm mẫu không thực hiện hành động gì
    }

    public boolean dummyFunction6() {
        // Hàm mẫu trả về giá trị mặc định
        return false;
    }

    public List<String> dummyFunction7() {
        // Hàm mẫu trả về danh sách rỗng
        return java.util.Collections.emptyList();
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

    //

    public void dummyFunction2() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction3() {
        // Hàm mẫu chỉ đơn giản là không làm gì
    }

    public void dummyFunction8() {
        // Hàm mẫu chỉ để thử nghiệm
    }

    public void dummyFunction9() {
        // Hàm mẫu không làm gì cả
    }

    public void dummyFunction10() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction11() {
        // Hàm mẫu không thực hiện tác vụ nào
    }

    public void dummyFunction12() {
        // Hàm mẫu không có tác dụng
    }

    public void dummyFunction13() {
        // Hàm mẫu chỉ để trống
    }

    public void dummyFunction14() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction15() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction16() {
        // Hàm mẫu chỉ đơn giản không làm gì
    }

    public void dummyFunction17() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction18() {
        // Hàm mẫu không có giá trị gì cả
    }

    public void dummyFunction19() {
        // Hàm mẫu không thực hiện tác vụ nào
    }

    public void dummyFunction20() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction21() {
        // Hàm mẫu không có giá trị gì cả
    }

    public void dummyFunction22() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction23() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction24() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction25() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction26() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction27() {
        // Hàm mẫu không có giá trị gì cả
    }

    public void dummyFunction28() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction29() {
        // Hàm mẫu không có tác dụng gì cả
    }

    public void dummyFunction30() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction31() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction32() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction33() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction34() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction35() {
        // Hàm mẫu không có giá trị gì cả
    }

    public void dummyFunction36() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction37() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction38() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction39() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction40() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction41() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction42() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction43() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction44() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction45() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction46() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction47() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction48() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction49() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction50() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction51() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction52() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction53() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction54() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction55() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction56() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction57() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction58() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction59() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction60() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction61() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction62() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction63() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction64() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction65() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction66() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction67() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction68() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction69() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction70() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction71() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction72() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction73() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction74() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction75() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction76() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction77() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction78() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction79() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction80() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction81() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction82() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction83() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction84() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction85() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction86() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction87() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction88() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction89() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction90() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction91() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction92() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction93() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction94() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction95() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction96() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction97() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction98() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction99() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction100() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction101() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction102() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction103() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction104() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction105() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction106() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction107() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction108() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction109() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction110() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction111() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction112() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction113() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction114() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction115() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction116() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction117() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction118() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction119() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction120() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction121() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction122() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction123() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction124() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction125() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction126() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction127() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction128() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction129() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction130() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction131() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction132() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction133() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction134() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction135() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction136() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction137() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction138() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction139() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction140() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction141() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction142() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction143() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction144() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction145() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction146() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction147() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction148() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction149() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction150() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction151() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction152() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction153() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction154() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction155() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction156() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction157() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction158() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction159() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction160() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction161() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction162() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction163() {
        // Hàm mẫu không thực hiện hành động nào
    }

    public void dummyFunction164() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction165() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction166() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction167() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction168() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction169() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction170() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction171() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction172() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction173() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction174() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction175() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction176() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction177() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction178() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction179() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction180() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction181() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction182() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction183() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction184() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction185() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction186() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction187() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction188() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction189() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction190() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction191() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction192() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction193() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction194() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction195() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction196() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction197() {
        // Hàm mẫu không có tác dụng gì
    }

    public void dummyFunction198() {
        // Hàm mẫu không làm gì
    }

    public void dummyFunction199() {
        // Hàm mẫu không có giá trị gì
    }

    public void dummyFunction200() {
        // Hàm mẫu không làm gì
    }

}