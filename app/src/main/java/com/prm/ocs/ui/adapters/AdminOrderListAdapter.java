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
    public AdminOrderListAdapter(Context context, List<OrderSummaryDto> initialOrders, OnOrderClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.orders = initialOrders; // Gán danh sách được truyền vào
        // Khởi tạo định dạng một lần trong constructor
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Thêm giờ phút nếu muốn
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    }

    // Phương thức setOrders để cập nhật dữ liệu
    public void setOrders(List<OrderSummaryDto> newOrders) {
        this.orders.clear(); // Xóa dữ liệu cũ
        if (newOrders != null) {
            this.orders.addAll(newOrders); // Thêm dữ liệu mới
        }
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderSummaryDto orderSummary = orders.get(position);

        // Sử dụng ID đầy đủ có thể quá dài, cân nhắc hiển thị vài ký tự cuối
        String shortOrderId = orderSummary.getOrder().getOrderId().toString();
        if (shortOrderId.length() > 8) {
            shortOrderId = "..." + shortOrderId.substring(shortOrderId.length() - 8);
        }
        holder.orderIdTextView.setText(shortOrderId); // Hiển thị ID rút gọn

        // Sử dụng SimpleDateFormat đã khởi tạo
        holder.orderDateTextView.setText(dateFormat.format(orderSummary.getOrder().getOrderDate()));
        holder.productCountTextView.setText(String.valueOf(orderSummary.getProductCount()));

        // Sử dụng NumberFormat để định dạng tiền tệ
        holder.totalAmountTextView.setText(currencyFormat.format(orderSummary.getTotalAmount()));
        String status = orderSummary.getOrder().getStatus().toLowerCase(); // Normalize status to lowercase
        holder.statusTextView.setText(status);
        if (status.equals("pending")) {
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.status_pending_color));
        } else if (status.equals("processing")) {
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.status_processing_color));
        } else if (status.equals("delivered")) {
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_color));
        } else if (status.equals("cancelled")) {
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.status_cancelled_color));
        } else {
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.default_text_color));
        }

        // Thiết lập listener cho toàn bộ item view
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(orderSummary);
            }
        });
    }

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