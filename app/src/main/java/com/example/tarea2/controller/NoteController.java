package com.example.tarea2.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.tarea2.model.AppDatabase;
import com.example.tarea2.model.Note;
import com.example.tarea2.model.NoteDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteController {

    private final NoteDao noteDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final HistoryController historyController;

    public NoteController(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.noteDao = database.noteDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.historyController = new HistoryController(context);
    }

    public interface NoteCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface NoteListCallback {
        void onSuccess(List<Note> notes);
        void onError(String error);
    }

    public void insertNote(Note note, NoteCallback callback) {
        executorService.execute(() -> {
            try {
                noteDao.insertNote(note);
                historyController.logAction("insert_note", "Nota: " + note.getNoteTitle());
                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void updateNote(Note note, NoteCallback callback) {
        executorService.execute(() -> {
            try {
                noteDao.updateNote(note);
                historyController.logAction("update_note", "Nota: " + note.getNoteTitle());
                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void deleteNote(Note note, NoteCallback callback) {
        executorService.execute(() -> {
            try {
                noteDao.deleteNote(note);
                historyController.logAction("delete_note", "Nota: " + note.getNoteTitle());
                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void getAllNotes(NoteListCallback callback) {
        executorService.execute(() -> {
            try {
                List<Note> notes = noteDao.getAllNotes();
                mainHandler.post(() -> callback.onSuccess(notes));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void getNotesByCategory(int categoryId, NoteListCallback callback) {
        executorService.execute(() -> {
            try {
                List<Note> notes = noteDao.getNotesByCategory(categoryId);
                mainHandler.post(() -> callback.onSuccess(notes));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void searchNotes(String searchText, NoteListCallback callback) {
        executorService.execute(() -> {
            try {
                List<Note> notes = noteDao.searchNotes(searchText);
                mainHandler.post(() -> callback.onSuccess(notes));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}