package com.prm.ocs.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.prm.ocs.R;
import com.prm.ocs.data.dto.OrderSummaryDto;

import java.text.NumberFormat; // Sử dụng NumberFormat để định dạng tiền tệ tốt hơn
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderListAdapter extends RecyclerView.Adapter<AdminOrderListAdapter.OrderViewHolder> {
    private Context context;
    private List<OrderSummaryDto> orders; // Không cần khởi tạo new ArrayList<>() ở đây nữa
    private OnOrderClickListener listener;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;


    public interface OnOrderClickListener {
        void onOrderClick(OrderSummaryDto order);
    }

    // Constructor mới nhận danh sách ban đầu

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0; // Kiểm tra null đề phòng
    }

    // OrderViewHolder giữ nguyên
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, orderDateTextView, productCountTextView, totalAmountTextView, statusTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.order_id);
            orderDateTextView = itemView.findViewById(R.id.order_date);
            productCountTextView = itemView.findViewById(R.id.product_count);
            totalAmountTextView = itemView.findViewById(R.id.total_amount);
            statusTextView = itemView.findViewById(R.id.status);
        }
    }
}