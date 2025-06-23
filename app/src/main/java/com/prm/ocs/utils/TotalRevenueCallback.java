package com.prm.ocs.utils;

public interface TotalRevenueCallback {
    void onTotalAmountCalculated(double totalAmount);
    void onError(String message);
}