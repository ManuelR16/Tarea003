package com.example.tarea2.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM notes")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE category_id = :categoryId")
    List<Note> getNotesByCategory(int categoryId);

    @Query("SELECT * FROM notes WHERE note_title LIKE '%' || :searchText || '%' OR note_content LIKE '%' || :searchText || '%'")
    List<Note> searchNotes(String searchText);
}