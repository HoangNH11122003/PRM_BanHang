package com.prm.ocs.ui.fragments.category;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.controller.CategoryController;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.data.db.entity.Category;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.ui.adapters.AdminProductListAdapter;
import com.prm.ocs.ui.fragments.product.AdminProductDetailFragment;
import com.prm.ocs.ui.view.base.ProductView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AdminCategoryDetailFragment extends Fragment implements CategoryController.CategoryDetailCallback, ProductView, AdminProductListAdapter.OnProductClickListener {

    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String TAG = "AdminCategoryDetail";

    private EditText nameEditText;
    private EditText descriptionEditText;
    private Button saveButton;
    private Button deleteButton;
    private RecyclerView productsRecyclerView;
    private AdminProductListAdapter adminProductListAdapter;

    private CategoryController categoryController;
    private ProductController productController;
    private Category currentCategory;

    private long loadStartTime;

    public static AdminCategoryDetailFragment newInstance(UUID categoryId) {
        AdminCategoryDetailFragment fragment = new AdminCategoryDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadStartTime = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.fragment_admin_category_detail, container, false);
        initializeViews(view);
        setupControllers();
        setupRecyclerView();

        UUID categoryId = getCategoryIdFromArgs();
        if (categoryId != null) {
            Log.d(TAG, "Loading category: " + categoryId);
            categoryController.loadCategoryDetails(categoryId, this);
            productController.loadProductsByCategory(categoryId);
        } else {
            showToast("Invalid category ID.");
            Log.e(TAG, "Category ID is null.");
        }

        setupButtonListeners();
        return view;
    }

    private void initializeViews(View view) {
        nameEditText = view.findViewById(R.id.category_name);
        descriptionEditText = view.findViewById(R.id.category_description);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
    }

    private void setupControllers() {
        categoryController = new CategoryController(getContext());
        productController = new ProductController(this);
    }

    private void setupRecyclerView() {
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adminProductListAdapter = new AdminProductListAdapter(getContext(), this);
        productsRecyclerView.setAdapter(adminProductListAdapter);
    }

    private UUID getCategoryIdFromArgs() {
        try {
            String idString = getArguments() != null ? getArguments().getString(ARG_CATEGORY_ID) : null;
            return idString != null ? UUID.fromString(idString) : null;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing category ID", e);
            return null;
        }
    }

    private void setupButtonListeners() {
        saveButton.setOnClickListener(v -> {
            Log.i(TAG, "Save button clicked.");
            updateCategory();
        });

        deleteButton.setOnClickListener(v -> {
            Log.i(TAG, "Delete button clicked.");
            deleteCategory();
        });
    }

    @Override
    public void onCategoryLoaded(Category category) {
        long elapsed = System.currentTimeMillis() - loadStartTime;
        Log.d(TAG, String.format(Locale.US, "Category loaded in %d ms", elapsed));
        currentCategory = category;

        if (category != null) {
            nameEditText.setText(category.getName());
            descriptionEditText.setText(category.getDescription());
        } else {
            Log.w(TAG, "Category is null after load.");
        }
    }

    @Override
    public void displayProducts(List<Product> products) {
        if (products == null) products = new ArrayList<>();
        Log.d(TAG, "Displaying " + products.size() + " products.");
        adminProductListAdapter.setProducts(products);
    }

    @Override
    public void displayProductDetails(Product product) {
        // Not used in this fragment
    }

    @Override
    public void onProductClick(UUID productId) {
        Log.d(TAG, "Product clicked: " + productId);
        AdminProductDetailFragment fragment = AdminProductDetailFragment.newInstance(productId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateCategory() {
        try {
            if (currentCategory == null) {
                Log.w(TAG, "Attempted to update with null category.");
                return;
            }

            String name = sanitize(nameEditText.getText().toString());
            String description = sanitize(descriptionEditText.getText().toString());

            if (!validateInput(name, description)) return;

            currentCategory.setName(name);
            currentCategory.setDescription(description);

            categoryController.updateCategory(currentCategory);
            showToast("Category saved successfully.");
            Log.i(TAG, "Category updated: " + currentCategory.getId());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Log.e(TAG, "Update failed", e);
            showToast("Update failed: " + e.getMessage());
        }
    }

    private void deleteCategory() {
        if (currentCategory == null) {
            Log.w(TAG, "Attempted to delete with null category.");
            return;
        }

        try {
            categoryController.deleteCategory(currentCategory);
            showToast("Category deleted.");
            Log.i(TAG, "Category deleted: " + currentCategory.getId());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Log.e(TAG, "Deletion failed", e);
            showToast("Delete failed: " + e.getMessage());
        }
    }

    private boolean validateInput(String name, String description) {
        if (TextUtils.isEmpty(name)) {
            showToast("Category name is required.");
            return false;
        }
        if (TextUtils.isEmpty(description)) {
            showToast("Category description is required.");
            return false;
        }
        if (name.length() > 50 || description.length() > 255) {
            showToast("Input too long.");
            return false;
        }
        return true;
    }

    private String sanitize(String input) {
        return input.trim().replaceAll("\\s{2,}", " ");
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
