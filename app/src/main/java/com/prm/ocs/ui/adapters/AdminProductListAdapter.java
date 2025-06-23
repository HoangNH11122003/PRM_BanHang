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
    public AdminProductListAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.products = new ArrayList<>(); // Khởi tạo danh sách rỗng nội bộ
    }

    // --- Constructor Gốc (Vẫn giữ lại để tương thích hoặc tùy chọn) ---

    /**
     * Constructor nhận danh sách sản phẩm ban đầu.
     *
     * @param context         Context
     * @param initialProducts Danh sách sản phẩm ban đầu. Adapter sẽ tạo một bản sao.
     * @param listener        Listener cho sự kiện click.
     */
    public AdminProductListAdapter(Context context, List<Product> initialProducts, OnProductClickListener listener) {
        this.context = context;
        this.listener = listener;
        // Tạo bản sao để quản lý nội bộ, tránh thay đổi từ bên ngoài
        this.products = (initialProducts != null) ? new ArrayList<>(initialProducts) : new ArrayList<>();
    }

    // Phương thức cập nhật danh sách (giữ nguyên)
    public void setProducts(List<Product> newProducts) {
        this.products.clear(); // Xóa danh sách nội bộ cũ
        if (newProducts != null) {
            this.products.addAll(newProducts); // Thêm dữ liệu mới vào danh sách nội bộ
        }
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position); // Lấy từ danh sách nội bộ

        holder.nameTextView.setText(product.getName());
        holder.importPriceTextView.setText("Giá nhập: " + currencyFormatter.format(product.getImportPrice()));
        holder.sellingPriceTextView.setText("Giá bán: " + currencyFormatter.format(product.getSellingPrice()));
        holder.stockTextView.setText("Tồn kho: " + product.getStock());
        holder.soldTextView.setText("Đã bán: " + product.getSold());

        // Logic load ảnh giữ nguyên
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            if (product.getImage().startsWith("http://") || product.getImage().startsWith("https://")) {
                Glide.with(context)
                        .load(product.getImage())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.imageView);
            } else if (product.getImage().startsWith("/") || product.getImage().contains("/")) {
                File imageFile = new File(product.getImage());
                Glide.with(context)
                        .load(imageFile)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.imageView);
            } else {
                loadImageFromDrawable(product.getImage(), holder.imageView);
            }
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_launcher_background)
                    .into(holder.imageView);
        }

        holder.imageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product.getProductId());
            }
        });

        holder.moreButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product.getProductId());
            }
        });
    }

    // loadImageFromDrawable giữ nguyên
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