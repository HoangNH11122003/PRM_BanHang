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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.prm.ocs.R;
import com.prm.ocs.data.db.entity.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders = new ArrayList<>();
    private Map<String, Map<UUID, String>> productInfo;
    private final Context context;
    private final OnOrderClickListener listener;

    public OrderAdapter(Context context, OnOrderClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setOrders(List<Order> orders, Map<String, Map<UUID, String>> productInfo) {
        this.orders = orders != null ? orders : new ArrayList<>();
        this.productInfo = productInfo;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String productName = "Unknown Item";
        String productImage = null;

        if (productInfo != null) {
            Map<UUID, String> productNames = productInfo.get("productNames");
            Map<UUID, String> productImages = productInfo.get("productImages");
            if (productNames != null && productNames.containsKey(order.getOrderId())) {
                productName = productNames.get(order.getOrderId());
            }
            if (productImages != null && productImages.containsKey(order.getOrderId())) {
                productImage = productImages.get(order.getOrderId());
            }
        }

        holder.orderDateText.setText(sdf.format(order.getOrderDate()));
        holder.itemNameText.setText(productName);
        holder.totalAmountText.setText(String.format(Locale.getDefault(), "%.2f$", order.getTotalAmount()));

        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(context)
                    .load(productImage)
                    .transform(new RoundedCorners(16)) // Bo góc với bán kính 16px
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.orderIcon);
        } else {
            holder.orderIcon.setImageResource(R.drawable.placeholder_image);
        }

        holder.viewInvoiceText.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView orderIcon;
        TextView orderDateText;
        TextView itemNameText;
        TextView viewInvoiceText;
        TextView totalAmountText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIcon = itemView.findViewById(R.id.order_icon);
            orderDateText = itemView.findViewById(R.id.order_date_text);
            itemNameText = itemView.findViewById(R.id.item_name_text);
            viewInvoiceText = itemView.findViewById(R.id.view_invoice_text);
            totalAmountText = itemView.findViewById(R.id.total_amount_text);
        }
    }
}