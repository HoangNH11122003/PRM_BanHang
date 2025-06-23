package com.prm.ocs.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.data.db.entity.OrderDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    private List<OrderDetail> orderDetails = new ArrayList<>();
    private Map<UUID, String> productNames;

    public void setOrderDetails(List<OrderDetail> orderDetails, Map<UUID, String> productNames) {
        this.orderDetails = orderDetails != null ? orderDetails : new ArrayList<>();
        this.productNames = productNames;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail detail = orderDetails.get(position);

        String productName = "Unknown Product";
        if (productNames != null && productNames.containsKey(detail.getOrderDetailId())) {
            productName = productNames.get(detail.getOrderDetailId());
        }

        holder.productNameText.setText(productName);
        holder.quantityText.setText("Qty: " + detail.getQuantity());
        holder.priceText.setText("$" + String.format(Locale.getDefault(), "%.2f", detail.getPrice()));
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView productNameText, quantityText, priceText;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameText = itemView.findViewById(R.id.detail_product_name);
            quantityText = itemView.findViewById(R.id.detail_quantity);
            priceText = itemView.findViewById(R.id.detail_price);
        }
    }
}