package com.example.tarea2.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert
    void insertHistory(History history);

    @Query("SELECT * FROM history ORDER BY history_id DESC")
    List<History> getAllHistory();

    @Query("SELECT * FROM history WHERE `action` = :actionType ORDER BY history_id DESC")
    List<History> getHistoryByAction(String actionType);

    @Query("SELECT * FROM history WHERE created_at LIKE :date || '%' ORDER BY history_id DESC")
    List<History> getHistoryByDate(String date);
}