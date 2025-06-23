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
import com.prm.ocs.data.dto.BrandProductsDto;
import com.prm.ocs.ui.view.brand.AdminBrandListActivity;

import java.util.ArrayList;
import java.util.List;

public class BrandListAdapter extends RecyclerView.Adapter<BrandListAdapter.BrandViewHolder> {
    private Context context;
    private List<BrandProductsDto> brands;

    public BrandListAdapter(Context context) {
        this.context = context;
        this.brands = new ArrayList<>();
    }

    public void setBrands(List<BrandProductsDto> brands) {
        this.brands = brands;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_brand_card, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        BrandProductsDto brand = brands.get(position);
        holder.nameTextView.setText(brand.getName());
        holder.productCountTextView.setText("Products: " + brand.getNumberOfProducts());

        holder.moreButton.setOnClickListener(v -> {
            ((AdminBrandListActivity) context).showBrandDetail(brand.getBrandId());
        });
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, productCountTextView;
        ImageButton moreButton;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.brand_name);
            productCountTextView = itemView.findViewById(R.id.brand_product_count);
            moreButton = itemView.findViewById(R.id.brand_more_button);
        }
    }
}