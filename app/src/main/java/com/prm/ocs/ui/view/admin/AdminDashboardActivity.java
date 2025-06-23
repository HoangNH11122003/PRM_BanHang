package com.prm.ocs.ui.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction; // Import this

import com.prm.ocs.R;
import com.prm.ocs.data.SeedData;
import com.prm.ocs.ui.fragments.admin.AdminDashboardStatsFragment; // Import the fragment
import com.prm.ocs.ui.manager.SessionManager;
import com.prm.ocs.ui.view.brand.AdminBrandListActivity;
import com.prm.ocs.ui.view.cart.LoginCheck;
import com.prm.ocs.ui.view.category.AdminCategoryListActivity;
import com.prm.ocs.ui.view.order.AdminOrderListActivity;
import com.prm.ocs.ui.view.product.AdminProductListActivity;
import com.prm.ocs.ui.view.user.AdminUserListActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Seed data (consider running this only once if needed)
        SeedData seedData = new SeedData(this);
        seedData.seedDatabase();

        // --- Toolbar Setup ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide default title
        }

        // --- Load Statistics Fragment ---
        if (savedInstanceState == null) { // Load only once
            AdminDashboardStatsFragment statsFragment = new AdminDashboardStatsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.stats_fragment_container, statsFragment); // Use replace or add
            transaction.commit();
        }


        // --- CardView Setup ---
        CardView usersCard = findViewById(R.id.users_button);
        CardView brandCard = findViewById(R.id.brand_button);
        CardView categoryCard = findViewById(R.id.category_button);
        CardView productCard = findViewById(R.id.product_button);
        CardView orderCard = findViewById(R.id.order_button);

        // --- Click Listeners ---
        usersCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminUserListActivity.class);
            startActivity(intent);
        });

        brandCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminBrandListActivity.class);
            startActivity(intent);
        });

        categoryCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminCategoryListActivity.class);
            startActivity(intent);
        });

        productCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminProductListActivity.class);
            startActivity(intent);
        });

        orderCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminOrderListActivity.class);
            startActivity(intent);
        });
    }

    // --- Options Menu ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            // Navigate to Admin Profile Activity/Fragment
            // Intent intent = new Intent(this, AdminUserProfileActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "Profile Clicked (Implement Navigation)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            // Logout Logic
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.clearSession();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginCheck.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish(); // Finish AdminDashboardActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}