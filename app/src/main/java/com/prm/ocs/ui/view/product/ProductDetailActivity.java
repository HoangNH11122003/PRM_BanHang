package com.prm.ocs.ui.view.product;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.prm.ocs.R;
import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.db.entity.Brand;
import com.prm.ocs.data.db.entity.Category;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.data.dto.CartDTO;
import com.prm.ocs.ui.manager.CartManager;
import com.prm.ocs.ui.manager.SessionManager;

import java.util.UUID;

public class ProductDetailActivity extends AppCompatActivity {


    private ImageView ivProductImage;
    private TextView tvProductName;
    private TextView tvSellingPrice;
    private TextView tvStock;
    private TextView tvSold;
    private Button btnBack;
    private Button btnAddToCart;
    private TextView tvCategory;
    private TextView tvBrand;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvSellingPrice = findViewById(R.id.tvSellingPrice);
        tvStock = findViewById(R.id.tvStock);
        tvSold = findViewById(R.id.tvSold);
        tvCategory = findViewById(R.id.tvCategory);
        tvBrand = findViewById(R.id.tvBrand);
        tvDescription = findViewById(R.id.tvDescription);
        btnBack = findViewById(R.id.btnBack);
        btnAddToCart = findViewById(R.id.btnAddToCart);


        String productId = getIntent().getStringExtra("productId");


        if (productId != null) {
            UUID productUUID = UUID.fromString(productId);


            DatabaseClient dbClient = DatabaseClient.getInstance(this);
            dbClient.getExecutorService().execute(() -> {

                Product product = dbClient.getAppDatabase().productDao().getProductById(productUUID);
                Category category = dbClient.getAppDatabase().categoryDao().getCategoryById(product.getCategoryId());
                Brand brand = dbClient.getAppDatabase().brandDao().getBrandById(product.getBrandId());

                runOnUiThread(() -> {
                    if (product != null) {

                        tvProductName.setText(product.getName());
                        tvSellingPrice.setText("$" + product.getSellingPrice());
                        tvStock.setText(String.valueOf(product.getStock()));
                        tvSold.setText(String.valueOf(product.getSold()));
                        tvDescription.setText(product.getDescription() != null ? product.getDescription() : "No description available");


                        String imageName = product.getImage();
                        int imageResId = getResources().getIdentifier(imageName.replace(".png", ""), "drawable", getPackageName());
                        Glide.with(this).load(imageResId).into(ivProductImage);


                        tvCategory.setText(category != null ? category.getName() : "N/A");
                        tvBrand.setText(brand != null ? brand.getName() : "N/A");
                    }
                });
            });
        }


        btnBack.setOnClickListener(v -> finish());


        btnAddToCart.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(this);
            UUID userId = UUID.fromString(sessionManager.getUserId());
            CartManager cartManager = new CartManager(this, userId.toString());
            CartDTO cart = cartManager.getCart();
            //check duplicate
            if (cart.getItems().stream().anyMatch(item -> item.getProductId().equals(UUID.fromString(productId)))) {
                Toast.makeText(this, "Product already in cart", Toast.LENGTH_SHORT).show();
                return;
            }
            cart.addItem(UUID.fromString(productId), 1);
            cartManager.saveCart(cart);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });
    }
}

