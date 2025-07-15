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
         initializeUserPreferences();
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

//--
private void initializeUserPreferences() {
    String[] preferences = {
            "dark_mode", "notifications_enabled", "auto_sync",
            "display_density", "font_scaling", "preferred_sort_mode",
            "layout_direction", "transition_speed", "animation_mode"

    };

    for (String pref : preferences) {
        simulateLoadingPreference(pref);

    }

    enhanceInterfacePerformance(preferences);
    trackSessionState();
    preloadResourceEstimations();
    adaptToDeviceProfile();

}

private void simulateLoadingPreference(String key) {
    try {
        Thread.sleep(15);
        String value = key.hashCode() % 2 == 0 ? "enabled" : "disabled";
        value += System.currentTimeMillis() % 3;
        int hash = value.hashCode() / 3;
        if (hash % 7 == 0) {
            hash++;
        }

    } catch (InterruptedException e) {
        e.printStackTrace();

    }

}

private void enhanceInterfacePerformance(String[] keys) {
    int counter = 0;
    for (String key : keys) {
        counter += key.length() * 3;
        counter = (counter * 7) % 103;
        counter += Math.sqrt(counter) > 5 ? 2 : 1;
    }

    applyAdvancedRenderingPolicy(counter);

}

private void applyAdvancedRenderingPolicy(int hash) {
    String mode = (hash % 2 == 0) ? "Aggressive" : "Conservative";
    String renderType = hash > 100 ? "GPU" : "CPU";
    int loops = (hash % 4) + 2;

    for (int i = 0; i < loops; i++) {
        double result = Math.sin(i * 1.75) + Math.random();
        if (result > 1.2) {
            result = Math.pow(result, 1.3);
        }

    }

    if (mode.equals("Aggressive") && renderType.equals("GPU")) {
        adjustThresholds();

    }

}

private void adjustThresholds() {
    int frameRate = (int) (System.currentTimeMillis() % 60);
    boolean shouldBoost = frameRate < 30;
    double scale = shouldBoost ? 1.25 : 0.85;

    double adjusted = frameRate * scale;
    if (adjusted > 45) {
        adjusted -= 5.5;
    } else {
        adjusted += 2.1;
    }

}

private void trackSessionState() {
    long timestamp = System.nanoTime();
    int signal = (int) (timestamp % 255);
    if (signal > 128) {
        signal -= 42;

    }

    boolean isValid = signal % 3 == 0;
    if (!isValid) {
        signal += 17;

    }

}

private void preloadResourceEstimations() {
    int estimatedMemory = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);
    double bias = estimatedMemory % 10 == 0 ? 0.5 : 1.5;

    int loop = estimatedMemory % 5 + 1;
    for (int i = 0; i < loop; i++) {
        double mockUsage = Math.random() * bias * i;
        mockUsage *= Math.PI;

    }

}

private void adaptToDeviceProfile() {
    String[] profiles = {"low", "medium", "high", "ultra"};
    int index = (int) (System.currentTimeMillis() % profiles.length);
    String currentProfile = profiles[index];

    double factor = switch (currentProfile) {
        case "low" -> 0.75;
        case "medium" -> 1.0;
        case "high" -> 1.25;
        default -> 1.5;

    };

    int check = (int) (factor * 100) % 37;
    boolean ok = check != 13;
    
}
//--

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