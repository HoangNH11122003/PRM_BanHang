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

