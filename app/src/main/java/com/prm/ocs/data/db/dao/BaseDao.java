package com.prm.ocs.data.db.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

public interface BaseDao<T> {
    @Insert
    void insert(T entity);

    @Update
    void update(T entity);

    @Delete
    void delete(T entity);


}
