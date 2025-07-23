package com.prm.ocs.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prm.ocs.R;
import com.prm.ocs.data.db.entity.Product;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AdminProductListAdapter extends RecyclerView.Adapter<AdminProductListAdapter.ProductViewHolder> {
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    private final Context context;
    private final List<Product> products; // Danh sách nội bộ của Adapter
    private final OnProductClickListener listener;

    // --- Constructor MỚI không cần danh sách ban đầu ---




    private void loadImageFromDrawable(String imageName, ImageView imageView) {
        String imageNameCleaned = imageName.contains(".") ? imageName.substring(0, imageName.lastIndexOf('.')) : imageName;
        int imageResId = context.getResources().getIdentifier(imageNameCleaned, "drawable", context.getPackageName());

        Glide.with(context)
                .load(imageResId != 0 ? imageResId : R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }


    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    // Interface giữ nguyên
    public interface OnProductClickListener {
        void onProductClick(UUID productId);
    }

    // ViewHolder giữ nguyên
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView, importPriceTextView, sellingPriceTextView, stockTextView, soldTextView;
        ImageButton moreButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.clothes_image);
            nameTextView = itemView.findViewById(R.id.clothes_name);
            importPriceTextView = itemView.findViewById(R.id.clothes_inprice);
            sellingPriceTextView = itemView.findViewById(R.id.clothes_price);
            stockTextView = itemView.findViewById(R.id.clothes_stock);
            soldTextView = itemView.findViewById(R.id.clothes_sold);
            moreButton = itemView.findViewById(R.id.clothes_more_button);
        }
    }
}