package com.prm.ocs.ui.fragments.brand;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prm.ocs.R;
import com.prm.ocs.controller.BrandController;
import com.prm.ocs.data.dto.BrandProductsDto;
import com.prm.ocs.ui.adapters.BrandListAdapter;

import java.util.List;

public class AdminBrandListFragment extends Fragment implements BrandController.BrandListCallback {
    private RecyclerView recyclerView;
    private BrandListAdapter adapter;
    private BrandController brandController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_brand_list, container, false);

        recyclerView = view.findViewById(R.id.brand_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BrandListAdapter(getContext());
        recyclerView.setAdapter(adapter);

        brandController = new BrandController(getContext());
        brandController.loadBrandsWithProductCount(this); // Sử dụng hàm trả DTO

        return view;
    }

    @Override
    public void onBrandsLoaded(List<BrandProductsDto> brands) {
        adapter.setBrands(brands);
    }

    //onResume
    @Override
    public void onResume() {
        super.onResume();
        brandController.loadBrandsWithProductCount(this);
    }
}