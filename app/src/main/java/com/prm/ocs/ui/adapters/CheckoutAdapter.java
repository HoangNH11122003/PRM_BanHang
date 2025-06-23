package com.prm.ocs.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prm.ocs.R;
import com.prm.ocs.data.dto.CartItemViewDTO;
import com.prm.ocs.data.db.entity.Product;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {
    private Context context;
    private List<CartItemViewDTO> cartItems;

    public CheckoutAdapter(Context context, List<CartItemViewDTO> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItemViewDTO cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        holder.tvProductName.setText(product.getName());
        holder.tvPrice.setText(String.format("$%.2f", product.getSellingPrice()));
        holder.tvQuantity.setText("Số lượng: " + cartItem.getQuantity());

        String imageName = product.getImage();
        if (imageName != null && !imageName.isEmpty()) {
//            int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
//            if (resId != 0) {
//                holder.ivProductImage.setImageResource(resId);
//            } else {
//                holder.ivProductImage.setImageResource(R.drawable.ic_launcher_background);
//            }
            int imageResId = context.getResources().getIdentifier(imageName.replace(".png", ""), "drawable", context.getPackageName());
            Glide.with(context)
                    .load(imageResId)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.not_found_img);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvPrice;
        TextView tvQuantity;

        CheckoutViewHolder(View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}