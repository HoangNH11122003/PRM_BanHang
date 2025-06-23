package com.prm.ocs.ui.fragments.category;

import android.os.Bundle;
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

import java.util.List;
import java.util.UUID;

public class AdminCategoryDetailFragment extends Fragment implements CategoryController.CategoryDetailCallback, ProductView, AdminProductListAdapter.OnProductClickListener {
    private static final String ARG_CATEGORY_ID = "category_id";
    private EditText nameEditText, descriptionEditText;
    private Button saveButton, deleteButton;
    private RecyclerView productsRecyclerView;
    private AdminProductListAdapter adminProductListAdapter;
    private CategoryController categoryController;
    private ProductController productController;
    private Category currentCategory;

    public static AdminCategoryDetailFragment newInstance(UUID categoryId) {
        AdminCategoryDetailFragment fragment = new AdminCategoryDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_category_detail, container, false);

        nameEditText = view.findViewById(R.id.category_name);
        descriptionEditText = view.findViewById(R.id.category_description);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);

        categoryController = new CategoryController(getContext());
        productController = new ProductController(this);

        // Khởi tạo RecyclerView với ProductAdapter
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adminProductListAdapter = new AdminProductListAdapter(getContext(), this);
        productsRecyclerView.setAdapter(adminProductListAdapter);

        UUID categoryId = UUID.fromString(getArguments().getString(ARG_CATEGORY_ID));
        categoryController.loadCategoryDetails(categoryId, this);
        productController.loadProductsByCategory(categoryId);

        saveButton.setOnClickListener(v -> updateCategory());
        deleteButton.setOnClickListener(v -> deleteCategory());

        return view;
    }

    @Override
    public void onCategoryLoaded(Category category) {
        currentCategory = category;
        if (category != null) {
            nameEditText.setText(category.getName());
            descriptionEditText.setText(category.getDescription());
        }
    }

    @Override
    public void displayProducts(List<Product> products) {
        adminProductListAdapter.setProducts(products);
    }

    @Override
    public void displayProductDetails(Product product) {
        // Không dùng trong Fragment này
    }

    @Override
    public void onProductClick(UUID productId) {
        // Xử lý khi nhấn nút "More" - ví dụ: mở AdminProductDetailFragment
        AdminProductDetailFragment fragment = AdminProductDetailFragment.newInstance(productId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateCategory() {
        try {
            if (currentCategory == null) return;

            currentCategory.setName(nameEditText.getText().toString());
            currentCategory.setDescription(descriptionEditText.getText().toString());
            categoryController.updateCategory(currentCategory);

            //validate
            if (currentCategory.getName().isEmpty()) {
                Toast.makeText(getContext(), "Category name is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentCategory.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Category description is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Category saved", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCategory() {
        if (currentCategory == null) return;

        categoryController.deleteCategory(currentCategory);
        getParentFragmentManager().popBackStack();
    }
}