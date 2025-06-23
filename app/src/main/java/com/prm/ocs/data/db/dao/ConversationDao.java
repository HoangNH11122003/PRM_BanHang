package com.prm.ocs.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.prm.ocs.data.db.entity.Conversation;
import java.util.List;
import java.util.UUID;

@Dao
public interface ConversationDao extends BaseDao<Conversation> {
    @Query("SELECT * FROM conversations")
    List<Conversation> getAllConversations();

    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    Conversation getConversationById(UUID conversationId);

    @Query("SELECT * FROM conversations WHERE userGuid = :userId")
    List<Conversation> getConversationsByUser(UUID userId);

    @Query("SELECT * FROM conversations WHERE adminGuid = :adminId")
    List<Conversation> getConversationsByAdmin(UUID adminId);
}