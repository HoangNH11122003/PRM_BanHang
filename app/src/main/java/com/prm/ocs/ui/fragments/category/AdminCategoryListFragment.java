package com.prm.ocs.ui.fragments.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prm.ocs.R;
import com.prm.ocs.controller.CategoryController;
import com.prm.ocs.data.dto.CategoryProductsDto;
import com.prm.ocs.ui.adapters.CategoryListAdapter;

import java.util.List;

public class AdminCategoryListFragment extends Fragment implements CategoryController.CategoryListCallback {
    private RecyclerView recyclerView;
    private CategoryListAdapter adapter;
    private CategoryController categoryController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_category_list, container, false);

        recyclerView = view.findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryListAdapter(getContext());
        recyclerView.setAdapter(adapter);

        categoryController = new CategoryController(getContext());
        categoryController.loadCategoriesWithProductCount(this); // Sử dụng hàm trả DTO

        return view;
    }

    @Override
    public void onCategoriesLoaded(List<CategoryProductsDto> categories) {
        adapter.setCategories(categories);
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryController.loadCategoriesWithProductCount(this);
    }
}