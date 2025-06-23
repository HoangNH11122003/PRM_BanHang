package com.prm.ocs.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.data.dto.OrderDetailWithProductName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderDetailAdapter extends RecyclerView.Adapter<AdminOrderDetailAdapter.OrderDetailViewHolder> {
    private final List<OrderDetailWithProductName> orderDetails;

    public AdminOrderDetailAdapter(List<OrderDetailWithProductName> orderDetails) {
        this.orderDetails = new ArrayList<>(orderDetails);
    }

    public void setOrderDetails(List<OrderDetailWithProductName> orderDetails) {
        this.orderDetails.clear();
        this.orderDetails.addAll(orderDetails);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_card, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetailWithProductName orderDetail = orderDetails.get(position);
        holder.productNameTextView.setText(orderDetail.getProductName() != null ? orderDetail.getProductName() : "N/A");
        holder.productIdTextView.setText(orderDetail.getProductId().toString());
        holder.quantityTextView.setText(String.valueOf(orderDetail.getQuantity()));
        holder.unitPriceTextView.setText(String.format(Locale.getDefault(), "%.2f", orderDetail.getPrice()));
        double totalPrice = orderDetail.getQuantity() * orderDetail.getPrice();
        holder.totalPriceTextView.setText(String.format(Locale.getDefault(), "%.2f", totalPrice));
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView, productIdTextView, quantityTextView, unitPriceTextView, totalPriceTextView;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.product_name);
            productIdTextView = itemView.findViewById(R.id.product_id);
            quantityTextView = itemView.findViewById(R.id.quantity);
            unitPriceTextView = itemView.findViewById(R.id.unit_price);
            totalPriceTextView = itemView.findViewById(R.id.total_price);
        }
    }
}