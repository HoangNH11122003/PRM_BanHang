package com.prm.ocs.ui.view.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prm.ocs.R;
import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.db.entity.Brand;
import com.prm.ocs.data.db.entity.Category;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.ui.adapters.ProductUserAdapter;
import com.prm.ocs.ui.view.cart.CartActivity;
import com.prm.ocs.ui.view.order.OrderHistoryActivity;

import java.util.ArrayList;
import java.util.List;

public class UserHomepageActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ProductUserAdapter productAdapter;
    private ImageButton cartButton;
    private ImageButton profileButton;
    private BottomNavigationView bottomNavigation;
    private List<Product> allProducts;
    private List<Category> categories;
    private List<Brand> brands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        // Khởi tạo các view
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        cartButton = findViewById(R.id.cartButton);
        profileButton = findViewById(R.id.profileButton);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Thiết lập RecyclerView
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductUserAdapter(this);
        recyclerViewProducts.setAdapter(productAdapter);

        // Khởi tạo danh sách
        allProducts = new ArrayList<>();
        categories = new ArrayList<>();
        brands = new ArrayList<>();

        // Tải dữ liệu ban đầu
        loadInitialData();

        // Sự kiện click nút giỏ hàng
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Go to Cart", Toast.LENGTH_SHORT).show();
        });

        // Sự kiện click nút profile
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Go to Profile", Toast.LENGTH_SHORT).show();
        });

        // Thiết lập Bottom Navigation
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_categories) {
                showCategoriesDialog();
                return true;
            } else if (itemId == R.id.nav_search) {
                showSearchDialog();
                return true;
            } else if (itemId == R.id.nav_brands) {
                showBrandsDialog();
                return true;
            } else if (itemId == R.id.nav_history) {
                Intent intent = new Intent(this, OrderHistoryActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Go to Order History", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadInitialData() {
        DatabaseClient dbClient = DatabaseClient.getInstance(this);
        dbClient.getExecutorService().execute(() -> {
            allProducts = dbClient.getAppDatabase().productDao().getAllProducts();
            categories = dbClient.getAppDatabase().categoryDao().getAllCategories();
            brands = dbClient.getAppDatabase().brandDao().getAllBrands();

            runOnUiThread(() -> {
                productAdapter.setProducts(allProducts); // Hiển thị tất cả sản phẩm ban đầu
            });
        });
    }

    private void showCategoriesDialog() {
        List<Object> categoryListWithAll = new ArrayList<>();
        categoryListWithAll.add("All");
        categoryListWithAll.addAll(categories);

        ListView listView = new ListView(this);
        ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, categoryListWithAll) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Object item = getItem(position);
                TextView textView = view.findViewById(android.R.id.text1);
                if (item instanceof String && "All".equals(item)) {
                    textView.setText("All Categories");
                } else if (item instanceof Category) {
                    textView.setText(((Category) item).getName());
                }
                return view;
            }
        };
        listView.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setView(listView)
                .setNegativeButton("Cancel", null)
                .create();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = adapter.getItem(position);
            if (selectedItem instanceof String && "All".equals(selectedItem)) {
                productAdapter.setProducts(allProducts);
            } else if (selectedItem instanceof Category) {
                Category selectedCategory = (Category) selectedItem;
                filterProductsByCategory(selectedCategory);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showBrandsDialog() {
        List<Object> brandListWithAll = new ArrayList<>();
        brandListWithAll.add("All");
        brandListWithAll.addAll(brands);

        ListView listView = new ListView(this);
        ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, brandListWithAll) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Object item = getItem(position);
                TextView textView = view.findViewById(android.R.id.text1);
                if (item instanceof String && "All".equals(item)) {
                    textView.setText("All Brands");
                } else if (item instanceof Brand) {
                    textView.setText(((Brand) item).getName());
                }
                return view;
            }
        };
        listView.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Brand")
                .setView(listView)
                .setNegativeButton("Cancel", null)
                .create();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = adapter.getItem(position);
            if (selectedItem instanceof String && "All".equals(selectedItem)) {
                productAdapter.setProducts(allProducts);
            } else if (selectedItem instanceof Brand) {
                Brand selectedBrand = (Brand) selectedItem;
                filterProductsByBrand(selectedBrand);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showSearchDialog() {
        EditText searchInput = new EditText(this);
        searchInput.setHint("Enter product name");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Search Products")
                .setView(searchInput)
                .setPositiveButton("Search", (d, which) -> {
                    String query = searchInput.getText().toString().trim();
                    if (!query.isEmpty()) {
                        productAdapter.filter(query);
                    } else {
                        productAdapter.setProducts(allProducts);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void filterProductsByCategory(Category category) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getCategoryId() != null && product.getCategoryId().equals(category.getCategoryId())) {
                filteredProducts.add(product);
            }
        }
        productAdapter.setProducts(filteredProducts);
    }

    private void filterProductsByBrand(Brand brand) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getBrandId() != null && product.getBrandId().equals(brand.getBrandId())) {
                filteredProducts.add(product);
            }
        }
        productAdapter.setProducts(filteredProducts);
    }

    //OnResume
    @Override
    protected void onResume() {
        super.onResume();
        loadInitialData();
    }
}