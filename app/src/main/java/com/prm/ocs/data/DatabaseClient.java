package com.prm.ocs.data;

import androidx.room.Room;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.prm.ocs.data.db.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseClient {
    private static DatabaseClient instance;
    private AppDatabase db;
    private ExecutorService executorService;
    private Handler mainHandler;

    private DatabaseClient(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration() // Thêm dòng này
                .build();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return db;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Handler getMainHandler() {
        return mainHandler;
    }
}