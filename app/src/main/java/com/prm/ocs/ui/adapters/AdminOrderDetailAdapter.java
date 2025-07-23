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


    }
}