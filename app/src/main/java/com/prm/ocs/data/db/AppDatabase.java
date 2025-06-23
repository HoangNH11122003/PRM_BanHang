package com.prm.ocs.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.prm.ocs.data.db.dao.*;
import com.prm.ocs.data.db.entity.*;

@Database(entities = {User.class, Product.class, Order.class, OrderDetail.class, Conversation.class, Message.class, Feedback.class, Category.class, Brand.class},
        version = 1,
        exportSchema = true
)
@TypeConverters({DateConverter.class})

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();
    public abstract ConversationDao conversationDao();
    public abstract MessageDao messageDao();
    public abstract FeedbackDao feedbackDao();
    public abstract CategoryDao categoryDao();
    public abstract BrandDao brandDao();
}