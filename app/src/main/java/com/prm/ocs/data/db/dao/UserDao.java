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




}