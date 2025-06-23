package com.prm.ocs.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.prm.ocs.data.db.entity.Message;
import java.util.List;
import java.util.UUID;

@Dao
public interface MessageDao extends BaseDao<Message> {
    @Query("SELECT * FROM messages")
    List<Message> getAllMessages();

    @Query("SELECT * FROM messages WHERE id = :messageId")
    Message getMessageById(UUID messageId);

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId")
    List<Message> getMessagesByConversation(UUID conversationId);

    @Query("SELECT * FROM messages WHERE senderId = :senderId")
    List<Message> getMessagesBySender(UUID senderId);
}