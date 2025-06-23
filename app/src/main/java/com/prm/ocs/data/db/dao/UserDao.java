package com.prm.ocs.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.prm.ocs.data.db.entity.User;

import java.util.List;
import java.util.UUID;

@Dao
public interface UserDao extends BaseDao<User> {
    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserById(UUID userId);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    @Query("Select Count(*) FROM users")
    int getUserCount();

    @Query("SELECT * FROM users WHERE userId = :userId AND password = :password")
    User checkUserPassword(UUID userId, String password);

    @Query("SELECT * FROM users WHERE username = :username OR email = :email OR phone = :phone LIMIT 1")
    User findByUsernameEmailOrPhone(String username, String email, String phone);

    @Query("SELECT * FROM users WHERE username = :username AND userId != :currentUserId LIMIT 1")
    User findByUsernameExcludingId(String username, UUID currentUserId);

    @Query("SELECT * FROM users WHERE email = :email AND userId != :currentUserId LIMIT 1")
    User findByEmailExcludingId(String email, UUID currentUserId);

    @Query("SELECT * FROM users WHERE phone = :phone AND userId != :currentUserId LIMIT 1")
    User findByPhoneExcludingId(String phone, UUID currentUserId);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findByUsernameForInsert(String username);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmailForInsert(String email);

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    User findByPhoneForInsert(String phone);
}