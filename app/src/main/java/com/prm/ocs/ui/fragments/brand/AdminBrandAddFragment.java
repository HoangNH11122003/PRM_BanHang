package com.prm.ocs.ui.fragments.brand;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.prm.ocs.R;
import com.prm.ocs.controller.BrandController;
import com.prm.ocs.data.db.entity.Brand;

import java.util.UUID;

public class AdminBrandAddFragment extends Fragment {
    private static final String ARG_BRAND_ID = "brand_id";
    private EditText nameEditText, descriptionEditText;
    private Button saveButton;
    private BrandController brandController;

    public static AdminBrandAddFragment newInstance(UUID brandId) {
        AdminBrandAddFragment fragment = new AdminBrandAddFragment();
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
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        brandController = new BrandController(getContext());

        saveButton.setOnClickListener(v -> addBrand());

        return view;
    }

    private void addBrand() {
        try {
            Brand newBrand = new Brand();
            newBrand.setBrandId(UUID.fromString(getArguments().getString(ARG_BRAND_ID)));
            newBrand.setName(nameEditText.getText().toString());
            newBrand.setDescription(descriptionEditText.getText().toString());
            if (newBrand.getName().isEmpty()) {
                Toast.makeText(getContext(), "Brand name is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newBrand.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Brand description is required!", Toast.LENGTH_SHORT).show();
                return;
            }


            brandController.addBrand(newBrand);
            Toast.makeText(getContext(), "Brand added", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
}