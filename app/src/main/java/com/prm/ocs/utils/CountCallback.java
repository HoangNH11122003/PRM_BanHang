package com.prm.ocs.utils;

public interface CountCallback {
    void onCountLoaded(int count);
    void onError(String message);
}