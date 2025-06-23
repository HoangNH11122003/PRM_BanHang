package com.prm.ocs.ui.view.cart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.data.dto.CartDTO;
import com.prm.ocs.data.dto.CartItemDTO;
import com.prm.ocs.data.dto.CartItemViewDTO;
import com.prm.ocs.ui.manager.CartManager;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.ui.adapters.CartAdapter;
import com.prm.ocs.ui.manager.SessionManager;
import com.prm.ocs.ui.view.base.ProductView;
import com.prm.ocs.ui.view.user.RegisterActivity;
import com.prm.ocs.ui.view.user.UserHomepageActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemChangeListener, ProductView {
    private RecyclerView recyclerViewCart;
    private Button btnCheckout;
    private CartManager cartManager;
    private List<CartItemViewDTO> cartItems;
    private CartAdapter cartAdapter;
    private ProductController productController;
    private List<Product> allProducts;
    private ExecutorService executorService;
    private Handler mainHandler;
    private SessionManager sessionManager;

    private TextView tvBackHome;

    private static final int REQUEST_CODE_CHECKOUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        sessionManager = new SessionManager(this);

        ImageButton btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> {
            finish();
            Toast.makeText(CartActivity.this, "Back to Home", Toast.LENGTH_SHORT).show();
        });

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login to view your cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginCheck.class));
            finish();
            return;
        }

        String userId = sessionManager.getUserId();
        cartManager = new CartManager(this, userId);
        cartItems = new ArrayList<>();
        allProducts = new ArrayList<>();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        productController = new ProductController((ProductView) this);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems, this);
        recyclerViewCart.setAdapter(cartAdapter);

        loadProducts();

        updateCheckoutButtonState();

        btnCheckout.setOnClickListener(v -> checkout());
    }

    private void loadProducts() {
        executorService.execute(() -> {
            mainHandler.post(() -> productController.loadProducts());
        });
    }

    private void initializeSampleCart() {
        if (allProducts == null || allProducts.size() < 3) {
            Toast.makeText(this, "Không đủ sản phẩm để tạo giỏ hàng mẫu!", Toast.LENGTH_SHORT).show();
            return;
        }

        CartDTO cart = cartManager.getCart();
        if (cart.getItems().isEmpty()) {
            Product nikeAirMax = allProducts.stream()
                    .filter(p -> p.getName().equals("Nike Air Max"))
                    .findFirst()
                    .orElse(null);
            Product adidasUltraboost = allProducts.stream()
                    .filter(p -> p.getName().equals("Adidas Ultraboost"))
                    .findFirst()
                    .orElse(null);
            Product zaraCasualDress = allProducts.stream()
                    .filter(p -> p.getName().equals("Zara Casual Dress"))
                    .findFirst()
                    .orElse(null);

            if (nikeAirMax != null) {
                cart.addItem(nikeAirMax.getProductId(), 2);
            }
            if (adidasUltraboost != null) {
                cart.addItem(adidasUltraboost.getProductId(), 1);
            }
            if (zaraCasualDress != null) {
                cart.addItem(zaraCasualDress.getProductId(), 1);
            }

            cartManager.saveCart(cart);
        }

        loadCart();
    }

    private void loadCart() {
        CartDTO cart = cartManager.getCart();
        cartItems.clear();
        if (cart.getItems().isEmpty()) {
            cartAdapter.notifyDataSetChanged();
            updateCheckoutButtonState();
            return;
        }

        for (CartItemDTO item : cart.getItems()) {
            productController.loadProductDetails(item.getProductId());
        }
    }

    private void updateCheckoutButtonState() {
        boolean isCartEmpty = cartItems.isEmpty();
        btnCheckout.setEnabled(!isCartEmpty);
        btnCheckout.setAlpha(isCartEmpty ? 0.5f : 1.0f);
    }

    private void checkout() {
        CartDTO cart = cartManager.getCart();
        if (cart.getItems().isEmpty()) {
            Toast.makeText(this, "Cart Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CHECKOUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHECKOUT && resultCode == RESULT_OK) {
            loadCart();
        }
    }

    @Override
    public void onCartItemChanged() {
        loadCart();
    }

    @Override
    public void displayProducts(List<Product> products) {
        this.allProducts = products;
        loadCart();
//        initializeSampleCart();
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
            cartAdapter.notifyDataSetChanged();
            updateCheckoutButtonState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
//        sessionManager.clearSession();
    }
}