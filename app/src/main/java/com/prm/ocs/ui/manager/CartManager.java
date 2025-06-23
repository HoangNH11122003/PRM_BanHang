package com.prm.ocs.ui.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prm.ocs.data.dto.CartDTO;
import com.prm.ocs.ui.adapters.UUIDTypeAdapter;

import java.util.UUID;

public class CartManager {
    private static final String CART_PREFS = "CartPrefs";
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private String userId;

    public CartManager(Context context, String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be null or empty");
        }
        this.userId = userId;
        this.sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
                .create();
    }

    public CartDTO getCart() {
        String cartKey = "Cart_" + userId;
        String cartJson = sharedPreferences.getString(cartKey, null);
        if (cartJson != null) {
            Log.d("CartManager", "Loaded cart for userId " + userId + ": " + cartJson);
            return gson.fromJson(cartJson, CartDTO.class);
        }
        return new CartDTO();
    }

    public void saveCart(CartDTO cart) {
        String cartKey = "Cart_" + userId;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cartJson = gson.toJson(cart);
        Log.d("CartManager", "Saving cart for userId " + userId + ": " + cartJson);
        editor.putString(cartKey, cartJson);
        editor.apply();
    }

    public void clearCart() {
        String cartKey = "Cart_" + userId;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(cartKey);
        editor.apply();
        Log.d("CartManager", "Cleared cart for userId " + userId);
    }
}