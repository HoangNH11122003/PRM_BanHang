package com.prm.ocs.ui.fragments.brand;

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
import com.prm.ocs.controller.BrandController;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.data.db.entity.Brand;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.ui.adapters.AdminProductListAdapter;
import com.prm.ocs.ui.fragments.product.AdminProductDetailFragment;
import com.prm.ocs.ui.view.base.ProductView;

import java.util.List;
import java.util.UUID;

public class AdminBrandDetailFragment extends Fragment implements BrandController.BrandDetailCallback, ProductView, AdminProductListAdapter.OnProductClickListener {
    private static final String ARG_BRAND_ID = "brand_id";
    private EditText nameEditText, descriptionEditText;
    private Button saveButton, deleteButton;
    private RecyclerView productsRecyclerView;
    private AdminProductListAdapter adminProductListAdapter;
    private BrandController brandController;
    private ProductController productController;
    private Brand currentBrand;

    public static AdminBrandDetailFragment newInstance(UUID brandId) {
        AdminBrandDetailFragment fragment = new AdminBrandDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BRAND_ID, brandId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_brand_detail, container, false);

        nameEditText = view.findViewById(R.id.brand_name);
        descriptionEditText = view.findViewById(R.id.brand_description);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);

        brandController = new BrandController(getContext());
        productController = new ProductController(this);

        // Khởi tạo RecyclerView với ProductAdapter
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adminProductListAdapter = new AdminProductListAdapter(getContext(), this);
        productsRecyclerView.setAdapter(adminProductListAdapter);

        UUID brandId = UUID.fromString(getArguments().getString(ARG_BRAND_ID));
        brandController.loadBrandDetails(brandId, this);
        productController.loadProductsByBrand(brandId);

        saveButton.setOnClickListener(v -> updateBrand());
        deleteButton.setOnClickListener(v -> deleteBrand());

        return view;
    }

    @Override
    public void onBrandLoaded(Brand brand) {
        currentBrand = brand;
        if (brand != null) {
            nameEditText.setText(brand.getName());
            descriptionEditText.setText(brand.getDescription());
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

    private void updateBrand() {
        try {
            if (currentBrand == null) return;

            currentBrand.setName(nameEditText.getText().toString());
            currentBrand.setDescription(descriptionEditText.getText().toString());
            brandController.updateBrand(currentBrand);

            //validate
            if (currentBrand.getName().isEmpty()) {
                Toast.makeText(getContext(), "Brand name is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentBrand.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Brand description is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Brand saved", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBrand() {
        if (currentBrand == null) return;
        brandController.deleteBrand(currentBrand);
        getParentFragmentManager().popBackStack();
    }
}