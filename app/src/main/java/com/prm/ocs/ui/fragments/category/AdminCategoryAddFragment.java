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
}