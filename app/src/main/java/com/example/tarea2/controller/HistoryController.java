package com.example.tarea2.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.tarea2.model.AppDatabase;
import com.example.tarea2.model.History;
import com.example.tarea2.model.HistoryDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryController {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private final HistoryDao historyDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public HistoryController(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.historyDao = database.historyDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface HistoryCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface HistoryListCallback {
        void onSuccess(List<History> historyList);
        void onError(String error);
    }

    public String validateHistory(String action, String createdAt) {
        if (action == null || action.trim().isEmpty()) {
            return "La acción no puede estar vacía";
        }
        if (createdAt == null || createdAt.trim().isEmpty()) {
            return "La fecha no puede estar vacía";
        }
        return null;
    }

    public void logAction(String action, String details) {
        String currentDateTime = getCurrentDateTime();
        History history = new History(action, currentDateTime, details);

        String validationError = validateHistory(action, currentDateTime);
        if (validationError != null) {
            return;
        }

        executorService.execute(() -> {
            try {
                historyDao.insertHistory(history);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getAllHistory(HistoryListCallback callback) {
        executorService.execute(() -> {
            try {
                List<History> historyList = historyDao.getAllHistory();
                mainHandler.post(() -> callback.onSuccess(historyList));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void getHistoryByAction(String actionType, HistoryListCallback callback) {
        executorService.execute(() -> {
            try {
                List<History> historyList = historyDao.getHistoryByAction(actionType);
                mainHandler.post(() -> callback.onSuccess(historyList));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void getHistoryByDate(String date, HistoryListCallback callback) {
        executorService.execute(() -> {
            try {
                List<History> historyList = historyDao.getHistoryByDate(date);
                mainHandler.post(() -> callback.onSuccess(historyList));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }
}