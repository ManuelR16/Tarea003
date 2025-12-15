package com.example.tarea2.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.tarea2.model.AppDatabase;
import com.example.tarea2.model.Category;
import com.example.tarea2.model.CategoryDao;
import com.example.tarea2.model.CategoryWithNotes;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryController {

    private final CategoryDao categoryDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final HistoryController historyController;

    public CategoryController(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.categoryDao = database.categoryDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.historyController = new HistoryController(context);
    }

    public interface CategoryCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface CategoryListCallback {
        void onSuccess(List<Category> categories);
        void onError(String error);
    }

    public interface CategoryWithNotesCallback {
        void onSuccess(List<CategoryWithNotes> categoriesWithNotes);
        void onError(String error);
    }

    public void insertCategory(Category category, CategoryCallback callback) {
        executorService.execute(() -> {
            try {
                categoryDao.insertCategory(category);
                historyController.logAction("insert_category", "Categoría: " + category.getCategoryName());
                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void updateCategory(Category category, CategoryCallback callback) {
        executorService.execute(() -> {
            try {
                categoryDao.updateCategory(category);
                historyController.logAction("update_category", "Categoría: " + category.getCategoryName());
                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void deleteCategory(Category category, CategoryCallback callback) {
        executorService.execute(() -> {
            try {
                categoryDao.deleteCategory(category);
                historyController.logAction("delete_category", "Categoría: " + category.getCategoryName());
                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void getAllCategories(CategoryListCallback callback) {
        executorService.execute(() -> {
            try {
                List<Category> categories = categoryDao.getAllCategories();
                mainHandler.post(() -> callback.onSuccess(categories));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void getCategoriesWithNotes(CategoryWithNotesCallback callback) {
        executorService.execute(() -> {
            try {
                List<CategoryWithNotes> categoriesWithNotes = categoryDao.getCategoriesWithNotes();
                mainHandler.post(() -> callback.onSuccess(categoriesWithNotes));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}