package com.prm.ocs.ui.fragments.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.prm.ocs.R;
import com.prm.ocs.controller.CategoryController;
import com.prm.ocs.data.db.entity.Category;

import java.util.UUID;

public class AdminCategoryAddFragment extends Fragment {
    private EditText nameEditText, descriptionEditText;
    private Button saveButton;
    private CategoryController categoryController;

    private static final String ARG_CATEGORY_ID = "category_id";

    public static AdminCategoryAddFragment newInstance(UUID categoryId) {
        AdminCategoryAddFragment fragment = new AdminCategoryAddFragment();
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
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        categoryController = new CategoryController(getContext());

        saveButton.setOnClickListener(v -> addCategory());

        return view;
    }

    private void addCategory() {
        try {
            Category newCategory = new Category();
            newCategory.setCategoryId(UUID.fromString(getArguments().getString(ARG_CATEGORY_ID)));
            newCategory.setName(nameEditText.getText().toString());
            newCategory.setDescription(descriptionEditText.getText().toString());

            if (newCategory.getName().isEmpty()) {
                Toast.makeText(getContext(), "Category name is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newCategory.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Category description is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            categoryController.addCategory(newCategory);
            Toast.makeText(getContext(), "Category added", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}