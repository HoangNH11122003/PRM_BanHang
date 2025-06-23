package com.prm.ocs.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prm.ocs.R;
import com.prm.ocs.data.dto.CartDTO;
import com.prm.ocs.data.dto.CartItemDTO;
import com.prm.ocs.data.dto.CartItemViewDTO;
import com.prm.ocs.ui.manager.CartManager;
import com.prm.ocs.ui.manager.SessionManager;

import java.util.List;
import java.util.UUID;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItemViewDTO> cartItems;
    private CartManager cartManager;
    private OnCartItemChangeListener listener;
    private Context context;
    private SessionManager sessionManager;

    public interface OnCartItemChangeListener {
        void onCartItemChanged();
    }

    public CartAdapter(Context context, List<CartItemViewDTO> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.context = context;
        this.sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be logged in to access cart");
        }
        this.cartManager = new CartManager(context, userId);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemViewDTO item = cartItems.get(position);
        holder.tvProductName.setText(item.getProduct().getName());
        holder.tvPrice.setText(String.valueOf(item.getProduct().getSellingPrice()) + "$");
        holder.etQuantity.setText(String.valueOf(item.getQuantity()));

        String imageName = item.getProduct().getImage();
        if (imageName != null && !imageName.isEmpty()) {
//            int resourceId = holder.itemView.getContext().getResources().getIdentifier(
//                    imageName, "drawable", holder.itemView.getContext().getPackageName());
//            if (resourceId != 0) {
//                holder.ivProductImage.setImageResource(resourceId);
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

        holder.btnUpdate.setOnClickListener(v -> {
            int newQuantity;
            try {
                newQuantity = Integer.parseInt(holder.etQuantity.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid quantity!", Toast.LENGTH_SHORT).show();
                return;
            }

            int stock = item.getProduct().getStock();
            if (newQuantity <= 0) {
                showRemoveConfirmationDialog(item.getProduct().getProductId());
                Toast.makeText(context, "Number invalid!", Toast.LENGTH_SHORT).show();
            } else if (newQuantity > stock) {
                Toast.makeText(context, "Larger quantity available, not enough stock!", Toast.LENGTH_SHORT).show();
            } else {
                updateItem(item.getProduct().getProductId(), newQuantity);
                Toast.makeText(context, "Update successfully!", Toast.LENGTH_SHORT).show();
                listener.onCartItemChanged();
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            showRemoveConfirmationDialog(item.getProduct().getProductId());
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void updateItem(UUID productId, int quantity) {
        CartDTO cart = cartManager.getCart();
        for (CartItemDTO item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        cartManager.saveCart(cart);
        for (CartItemViewDTO viewItem : cartItems) {
            if (viewItem.getProduct().getProductId().equals(productId)) {
                viewItem.setQuantity(quantity);
                break;
            }
        }
        notifyDataSetChanged();
    }

    private void removeItem(UUID productId) {
        CartDTO cart = cartManager.getCart();
        cart.removeItem(productId);
        cartManager.saveCart(cart);
        cartItems.removeIf(item -> item.getProduct().getProductId().equals(productId));
        notifyDataSetChanged();
        listener.onCartItemChanged();
    }

    private void showRemoveConfirmationDialog(UUID productId) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm")
                .setMessage("Do you want to remove this product from your cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    removeItem(productId);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice;
        EditText etQuantity;
        Button btnUpdate, btnRemove;
        ImageView ivProductImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            etQuantity = itemView.findViewById(R.id.etQuantity);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}