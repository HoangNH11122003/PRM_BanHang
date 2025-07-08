// package com.prm.ocs.ui.view.product;

// import android.os.Bundle;
// import android.view.View;
// import android.widget.ProgressBar;
// import android.widget.Toast;

// import androidx.appcompat.app.AppCompatActivity;
// import androidx.recyclerview.widget.LinearLayoutManager;
// import androidx.recyclerview.widget.RecyclerView;

// import com.prm.ocs.R;
// import com.prm.ocs.controller.ProductController;
// import com.prm.ocs.data.SeedData;
// import com.prm.ocs.data.db.entity.Product;
// import com.prm.ocs.ui.adapters.ProductUserAdapter;
// import com.prm.ocs.ui.view.base.ProductView;

// import java.util.List;

// public class UserProductActivity extends AppCompatActivity implements ProductView {

//     private RecyclerView recyclerView;
//     private ProgressBar progressBar;
//     private ProductUserAdapter productAdapter;
//     private ProductController productController;

//     @Override
//     protected void onCreate(Bundle savedInstanceState) {
//         super.onCreate(savedInstanceState);
//         setContentView(R.layout.activity_user_product);

//         // Seed dữ liệu mẫu vào database
//         new SeedData(this).seedDatabase();

//         // Khởi tạo các view
//         recyclerView = findViewById(R.id.recyclerViewProducts);
//         progressBar = findViewById(R.id.progressBar);

//         recyclerView.setLayoutManager(new LinearLayoutManager(this));
//         productAdapter = new ProductUserAdapter(this);
//         recyclerView.setAdapter(productAdapter);

//         productController = new ProductController(this);
//         loadProducts();
//     }

//     private void loadProducts() {
//         progressBar.setVisibility(View.VISIBLE);
//         productController.loadProducts();
//     }

//     @Override
//     public void displayProducts(List<Product> products) {
//         runOnUiThread(() -> {
//             progressBar.setVisibility(View.GONE);
//             if (products != null && !products.isEmpty()) {
//                 productAdapter.setProducts(products);
//             } else {
//                 Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
//             }
//         });
//     }

//     @Override
//     public void displayProductDetails(Product product) {
//         // Chưa sử dụng trong phiên bản gốc
//     }
// }


package com.prm.ocs.ui.view.product;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.data.SeedData;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.ui.adapters.ProductUserAdapter;
import com.prm.ocs.ui.view.base.ProductView;

import java.util.List;
import java.util.UUID;

public class UserProductActivity extends AppCompatActivity implements ProductView {
    private RecyclerView recyclerView;
    private ProductUserAdapter productAdapter;
    private ProgressBar progressBar;
    private ProductController productController;
    private androidx.appcompat.widget.SearchView searchView;
    private Spinner sortSpinner;
    private UUID categoryId;
    private UUID brandId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product);
        SeedData sd = new SeedData(this);
        sd.seedDatabase();




        // Khởi tạo các view
        recyclerView = findViewById(R.id.recyclerViewProducts);
        progressBar = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.searchView);
        sortSpinner = findViewById(R.id.sortSpinner);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductUserAdapter(this);
        recyclerView.setAdapter(productAdapter);


        productController = new ProductController((ProductView) this);


        String categoryIdString = getIntent().getStringExtra("categoryId");
        String brandIdString = getIntent().getStringExtra("brandId");


        if (categoryIdString != null) {
            categoryId = UUID.fromString(categoryIdString);
        }

        if (brandIdString != null) {
            brandId = UUID.fromString(brandIdString);
        }

        sortSpinner.setSelection(1);


        loadProducts();


        setupSearchView();


        setupSortSpinner();
    }

//    private void loadProducts() {
//        progressBar.setVisibility(View.VISIBLE);
//        productController.loadProducts();
//    }
private void loadProducts() {
    if (categoryId != null) {
        productController.loadProductsByCategory(categoryId.toString());
    } else if (brandId != null) {
        productController.loadProductsByBrand(brandId.toString());
    } else {
        productController.loadProducts();
    }
}

    @Override
    public void displayProducts(List<Product> products) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            if (products != null && !products.isEmpty()) {
                productAdapter.setProducts(products);

                productAdapter.sortProducts(true);
            } else {
                Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void displayProductDetails(Product product) {

    }

    private void setupSearchView() {

        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.isEmpty()) {
                    categoryId = null;
                    brandId = null;
                    loadProducts();
                } else {
                    productAdapter.filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        int searchIconId = androidx.appcompat.R.id.search_mag_icon;
        View searchIcon = searchView.findViewById(searchIconId);
        if (searchIcon != null) {
            searchIcon.setOnClickListener(v -> {
                String query = searchView.getQuery().toString();
                if (!query.isEmpty()) {
                    productAdapter.filter(query);
                } else {
                    categoryId = null;
                    brandId = null;
                    loadProducts();
                }
            });
        }
    }



    private void setupSortSpinner() {
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        productAdapter.sortProducts(false);
                        break;
                    case 1:
                        productAdapter.sortProducts(true);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}