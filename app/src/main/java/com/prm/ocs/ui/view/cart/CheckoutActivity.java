package com.prm.ocs.ui.view.cart;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.controller.OrderController;
import com.prm.ocs.controller.OrderDetailController;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.data.db.entity.Order;
import com.prm.ocs.data.db.entity.OrderDetail;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.data.dto.CartDTO;
import com.prm.ocs.data.dto.CartItemDTO;
import com.prm.ocs.data.dto.CartItemViewDTO;
import com.prm.ocs.ui.adapters.CheckoutAdapter;
import com.prm.ocs.ui.manager.CartManager;
import com.prm.ocs.ui.manager.SessionManager;
import com.prm.ocs.ui.view.base.ProductView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckoutActivity extends AppCompatActivity implements ProductView {
    private RecyclerView recyclerViewCheckout;
    private TextView tvTotalAmount;
    private EditText etShippingAddress;
    private Button btnConfirmCheckout;
    private CheckoutAdapter checkoutAdapter;
    private List<CartItemViewDTO> cartItems;
    private CartManager cartManager;
    private OrderController orderController;
    private OrderDetailController orderDetailController;
    private SessionManager sessionManager;
    private ProductController productController;
    private double totalAmount;
    private ExecutorService executorService;
    private Handler mainHandler;

    private TextView tvBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recyclerViewCheckout = findViewById(R.id.recyclerViewCheckout);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        etShippingAddress = findViewById(R.id.etShippingAddress);
        btnConfirmCheckout = findViewById(R.id.btnConfirmCheckout);
        // Trong onCreate
        ImageButton btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> {
            finish();
            Toast.makeText(CheckoutActivity.this, "Back to Cart", Toast.LENGTH_SHORT).show();
        });

        sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartManager = new CartManager(this, userId);
        orderController = new OrderController(this);
        orderDetailController = new OrderDetailController(this);
        productController = new ProductController((ProductView) this);

        cartItems = new ArrayList<>();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        recyclerViewCheckout.setLayoutManager(new LinearLayoutManager(this));
        checkoutAdapter = new CheckoutAdapter(this, cartItems);
        recyclerViewCheckout.setAdapter(checkoutAdapter);

        loadCart();

        btnConfirmCheckout.setOnClickListener(v -> confirmCheckout());
    }

    private void loadCart() {
        CartDTO cart = cartManager.getCart();
        cartItems.clear();
        if (cart.getItems().isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            checkoutAdapter.notifyDataSetChanged();
            updateTotalAmount();
            return;
        }

        for (CartItemDTO item : cart.getItems()) {
            productController.loadProductDetails(item.getProductId());
        }
    }

    private double calculateTotalAmount() {
        double total = 0;
        for (CartItemViewDTO item : cartItems) {
            total += item.getProduct().getSellingPrice() * item.getQuantity();
        }
        return total;
    }

    private void updateTotalAmount() {
        totalAmount = calculateTotalAmount();
        tvTotalAmount.setText(String.format("Total money: %.2f$", totalAmount));
    }

    private void confirmCheckout() {
        String address = etShippingAddress.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter shipping address!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userIdString = sessionManager.getUserId();
        if (userIdString == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }
        UUID userId = UUID.fromString(userIdString);

        // Tạo đơn hàng
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(new Date());
        order.setTotalAmount(totalAmount);
        order.setAddress(address);
        order.setStatus("Pending");

        List<OrderDetail> orderDetails = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();

        // Kiểm tra và chuẩn bị dữ liệu
        for (CartItemViewDTO item : cartItems) {
            Product product = item.getProduct();
            int requestedQuantity = item.getQuantity();

            // Kiểm tra số lượng tồn kho
            if (product.getStock() < requestedQuantity) {
                Toast.makeText(this, "Not enough stock for " + product.getName(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo chi tiết đơn hàng
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getOrderId());
            detail.setProductId(product.getProductId());
            detail.setQuantity(requestedQuantity);
            detail.setPrice(product.getSellingPrice());
            orderDetails.add(detail);

            // Cập nhật stock và sold cho sản phẩm
            product.setStock(product.getStock() - requestedQuantity);
            product.setSold(product.getSold() + requestedQuantity);
            productsToUpdate.add(product);
        }

        // Thực hiện giao dịch trên luồng nền
        executorService.execute(() -> {
            try {
                // Thêm đơn hàng
                orderController.addOrder(order);

                // Thêm chi tiết đơn hàng
                orderDetailController.addOrderDetails(orderDetails);

                // Cập nhật sản phẩm (stock và sold)
                for (Product product : productsToUpdate) {
                    productController.updateProduct(product);
                }

                // Xóa giỏ hàng sau khi thành công
                cartManager.clearCart();

                // Hiển thị thông báo trên luồng chính
                mainHandler.post(() -> {
                    Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(this, "Checkout failed!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void displayProducts(List<Product> products) {
    }

    @Override
    public void displayProductDetails(Product product) {
        if (product != null) {
            CartDTO cart = cartManager.getCart();
            for (CartItemDTO item : cart.getItems()) {
                if (item.getProductId().equals(product.getProductId())) {
                    cartItems.add(new CartItemViewDTO(product, item.getQuantity()));
                    break;
                }
            }
            checkoutAdapter.notifyDataSetChanged();
            updateTotalAmount();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}