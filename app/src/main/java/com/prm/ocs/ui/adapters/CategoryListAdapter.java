package com.prm.ocs.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm.ocs.R;
import com.prm.ocs.data.dto.CategoryProductsDto;
import com.prm.ocs.ui.view.category.AdminCategoryListActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> {
    private Context context;
    private List<CategoryProductsDto> categories;

    public CategoryListAdapter(Context context) {
        this.context = context;
        this.categories = new ArrayList<>();
    }

    public void setCategories(List<CategoryProductsDto> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_card, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryProductsDto category = categories.get(position);
        holder.nameTextView.setText(category.getName());
        holder.productCountTextView.setText("Products: " + category.getNumberOfProducts());

        holder.moreButton.setOnClickListener(v -> {
            ((AdminCategoryListActivity) context).showCategoryDetail(category.getCategoryId());
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, productCountTextView;
        ImageButton moreButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.category_name);
            productCountTextView = itemView.findViewById(R.id.category_product_count);
            moreButton = itemView.findViewById(R.id.category_more_button);
        }
    }
}