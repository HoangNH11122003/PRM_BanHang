package com.prm.ocs.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.prm.ocs.data.db.entity.Feedback;
import java.util.List;
import java.util.UUID;

@Dao
public interface FeedbackDao extends BaseDao<Feedback> {
    @Query("SELECT * FROM feedbacks")
    List<Feedback> getAllFeedbacks();

    @Query("SELECT * FROM feedbacks WHERE feedbackId = :feedbackId")
    Feedback getFeedbackById(UUID feedbackId);

    @Query("SELECT * FROM feedbacks WHERE userId = :userId")
    List<Feedback> getFeedbacksByUser(UUID userId);

    @Query("SELECT * FROM feedbacks WHERE productId = :productId")
    List<Feedback> getFeedbacksByProduct(UUID productId);
}