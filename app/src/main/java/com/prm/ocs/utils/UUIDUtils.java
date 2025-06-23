package com.prm.ocs.utils;

import android.util.Log;

import java.util.UUID;

public class UUIDUtils {
    private static final String TAG = "UUIDUtils";

    public static UUID parseUUID(String uuidString) {
        if (uuidString == null || uuidString.trim().isEmpty()) {
            Log.w(TAG, "UUID string is null or empty");
            return null;
        }

        // Loại bỏ khoảng trắng và các ký tự không cần thiết
        uuidString = uuidString.trim();

        try {
            // Nếu chuỗi có 32 ký tự (không có dấu gạch nối), thêm dấu gạch nối vào đúng vị trí
            if (uuidString.length() == 32) {
                uuidString = new StringBuilder(uuidString)
                        .insert(8, "-")
                        .insert(13, "-")
                        .insert(18, "-")
                        .insert(23, "-")
                        .toString();
            }

            // Parse UUID từ chuỗi
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid UUID format: " + uuidString, e);
            return null;
        }
    }
}