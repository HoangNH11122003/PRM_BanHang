package com.prm.ocs.ui.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.prm.ocs.ui.view.product.ProductDetailActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ProductUserAdapter extends RecyclerView.Adapter<ProductUserAdapter.ProductViewHolder> {
    private final Context context;
    private List<Product> products;
    private List<Product> filteredProducts;

    public ProductUserAdapter(Context context) {
        this.context = context;
        this.products = new ArrayList<>();
        this.filteredProducts = new ArrayList<>();
    }

    public void setProducts(List<Product> products) {
        this.products = new ArrayList<>(products);
        this.filteredProducts = new ArrayList<>(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = filteredProducts.get(position);

        holder.nameTextView.setText(product.getName());
        holder.sellingPriceTextView.setText("Selling Price: $" + product.getSellingPrice());
        holder.stockTextView.setText("Stock: " + product.getStock());
        holder.soldTextView.setText("Sold: " + product.getSold());

        // --- SỬA LOGIC LOAD ẢNH ---
        String imageNameOrPath = product.getImage(); // Lấy tên/đường dẫn/url ảnh

        // Kiểm tra xem imageNameOrPath có giá trị không
        if (imageNameOrPath != null && !imageNameOrPath.isEmpty()) {
            // Ưu tiên load từ URL nếu là URL
            if (imageNameOrPath.startsWith("http://") || imageNameOrPath.startsWith("https://")) {
                Glide.with(context)
                        .load(imageNameOrPath)
                        .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ load
                        .error(R.drawable.ic_launcher_background) // Ảnh lỗi
                        .into(holder.imageView);
            }
            // Kiểm tra xem có phải là đường dẫn file cục bộ không
            else if (imageNameOrPath.startsWith("/") || imageNameOrPath.contains("/")) { // Kiểm tra cơ bản
                File imageFile = new File(imageNameOrPath);
                Glide.with(context)
                        .load(imageFile) // Glide có thể tự xử lý lỗi file không tồn tại
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.imageView);
            }
            // Nếu không phải URL hoặc đường dẫn, thử load từ drawable resource
            else {
                // Inline logic của loadImageFromDrawable vào đây cho gọn
                String imageNameCleaned = imageNameOrPath.contains(".") ? imageNameOrPath.substring(0, imageNameOrPath.lastIndexOf('.')) : imageNameOrPath;
                int imageResId = context.getResources().getIdentifier(imageNameCleaned, "drawable", context.getPackageName());

                Glide.with(context)
                        .load(imageResId != 0 ? imageResId : R.drawable.ic_launcher_background) // Load ảnh lỗi nếu ID = 0
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.imageView);
            }
        } else {
            // Nếu imageNameOrPath là null hoặc rỗng, load ảnh mặc định
            Glide.with(context)
                    .load(R.drawable.ic_launcher_background) // Ảnh mặc định/lỗi
                    .into(holder.imageView);
        }
        // --- KẾT THÚC SỬA LOGIC LOAD ẢNH ---

        // Thiết lập sự kiện click cho nút "more"
        holder.moreButton.setOnClickListener(v -> {

            Intent intent = new Intent(context, ProductDetailActivity.class);

            intent.putExtra("productId", product.getProductId().toString());
            context.startActivity(intent);


        });
    }

    private void putProductDataIntoIntent(Intent intent, Product product) {
        intent.putExtra("productName", product.getName());
        intent.putExtra("productPrice", product.getSellingPrice());
        intent.putExtra("productStock", product.getStock());
        intent.putExtra("productSold", product.getSold());
        intent.putExtra("productDescription", product.getDescription());
        intent.putExtra("productImage", product.getImage());
        intent.putExtra("productId", product.getProductId().toString());
    }

    @Override
    public int getItemCount() {
        return filteredProducts.size();
    }


    public void filter(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(products);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Product product : products) {
                if (product.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredProducts.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }


    public void sortProducts(boolean ascending) {
        Collections.sort(filteredProducts, new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                if (ascending) {
                    return Double.compare(p1.getSellingPrice(), p2.getSellingPrice());
                } else {
                    return Double.compare(p2.getSellingPrice(), p1.getSellingPrice());
                }
            }
        });
        notifyDataSetChanged();
    }


    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView, sellingPriceTextView, stockTextView, soldTextView;
        ImageButton moreButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.clothes_image);
            nameTextView = itemView.findViewById(R.id.clothes_name);
            sellingPriceTextView = itemView.findViewById(R.id.clothes_price);
            stockTextView = itemView.findViewById(R.id.clothes_stock);
            soldTextView = itemView.findViewById(R.id.clothes_sold);
            moreButton = itemView.findViewById(R.id.clothes_more_button);
        }
    }
}