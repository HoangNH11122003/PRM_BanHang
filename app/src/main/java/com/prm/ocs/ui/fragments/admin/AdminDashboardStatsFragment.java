package com.prm.ocs.ui.fragments.admin; // Or your chosen package

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prm.ocs.R;
import com.prm.ocs.controller.BrandController;
import com.prm.ocs.controller.CategoryController;
import com.prm.ocs.controller.OrderController;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.utils.CountCallback; // Import callbacks
import com.prm.ocs.utils.TotalRevenueCallback;

import java.text.NumberFormat;
import java.util.Locale;

public class AdminDashboardStatsFragment extends Fragment {

    private static final String TAG = "AdminStatsFragment";

    private TextView tvUserCount, tvBrandCount, tvCategoryCount, tvProductCount, tvOrderCount, tvTotalRevenue;
    private UserController userController;
    private BrandController brandController;
    private CategoryController categoryController;
    private ProductController productController;
    private OrderController orderController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard_stats, container, false);

        tvUserCount = view.findViewById(R.id.tv_user_count);
        tvBrandCount = view.findViewById(R.id.tv_brand_count);
        tvCategoryCount = view.findViewById(R.id.tv_category_count);
        tvProductCount = view.findViewById(R.id.tv_product_count);
        tvOrderCount = view.findViewById(R.id.tv_order_count);
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);

        // Initialize Controllers
        userController = new UserController(requireContext());
        brandController = new BrandController(requireContext());
        categoryController = new CategoryController(requireContext());
        productController = new ProductController(requireContext());
        orderController = new OrderController(requireContext()); // Assuming context-only constructor exists

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadStatistics(); // Load data when the view is ready
    }

    private void loadStatistics() {
        // Load User Count
        userController.getUserCount(new CountCallback() {
            @Override
            public void onCountLoaded(int count) {
                tvUserCount.setText(String.valueOf(count));
            }
            @Override
            public void onError(String message) {
                Log.e(TAG, message);
                tvUserCount.setText("Error");
                // Optionally show a Toast
                // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Load Brand Count
        brandController.getBrandCount(new CountCallback() {
            @Override
            public void onCountLoaded(int count) {
                tvBrandCount.setText(String.valueOf(count));
            }
            @Override
            public void onError(String message) {
                Log.e(TAG, message);
                tvBrandCount.setText("Error");
            }
        });

        // Load Category Count
        categoryController.getCategoryCount(new CountCallback() {
            @Override
            public void onCountLoaded(int count) {
                tvCategoryCount.setText(String.valueOf(count));
            }
            @Override
            public void onError(String message) {
                Log.e(TAG, message);
                tvCategoryCount.setText("Error");
            }
        });

        // Load Product Count
        productController.getProductCount(new CountCallback() {
            @Override
            public void onCountLoaded(int count) {
                tvProductCount.setText(String.valueOf(count));
            }
            @Override
            public void onError(String message) {
                Log.e(TAG, message);
                tvProductCount.setText("Error");
            }
        });

        // Load Order Count
        orderController.getOrderCount(new CountCallback() {
            @Override
            public void onCountLoaded(int count) {
                tvOrderCount.setText(String.valueOf(count));
            }
            @Override
            public void onError(String message) {
                Log.e(TAG, message);
                tvOrderCount.setText("Error");
            }
        });


        // Load Total Revenue
        orderController.calculateTotalAmountForAllOrders(new TotalRevenueCallback() {
            @Override
            public void onTotalAmountCalculated(double totalAmount) {
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
                tvTotalRevenue.setText(format.format(totalAmount));
            }
            @Override
            public void onError(String message) {
                Log.e(TAG, message);
                tvTotalRevenue.setText("Error");
            }
        });
    }
}