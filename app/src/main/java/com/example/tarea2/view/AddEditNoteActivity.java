package com.example.tarea2.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tarea2.R;
import com.example.tarea2.controller.CategoryController;
import com.example.tarea2.controller.NoteController;
import com.example.tarea2.model.Category;
import com.example.tarea2.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditNoteActivity extends AppCompatActivity {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

    private EditText etNoteTitle;
    private EditText etNoteContent;
    private Spinner spinnerCategory;
    private Button btnSave;
    private Button btnDelete;
    private Button btnAddCategory;

    private NoteController noteController;
    private CategoryController categoryController;
    private boolean isEditMode = false;
    private int noteId;
    private String createdAt;

    private List<Category> categories;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        initViews();
        initControllers();
        loadCategories();
        checkEditMode();
        setupListeners();
    }

    private void initViews() {
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnAddCategory = findViewById(R.id.btnAddCategory);
    }

    private void initControllers() {
        noteController = new NoteController(this);
        categoryController = new CategoryController(this);
    }

    private void loadCategories() {
        categoryController.getAllCategories(new CategoryController.CategoryListCallback() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                categories = categoryList;
                List<String> categoryNames = new ArrayList<>();
                for (Category category : categories) {
                    categoryNames.add(category.getCategoryName());
                }

                categoryAdapter = new ArrayAdapter<>(
                        AddEditNoteActivity.this,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                );
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);

                if (isEditMode) {
                    setSelectedCategory();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditNoteActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEditMode() {
        if (getIntent().hasExtra("NOTE_ID")) {
            isEditMode = true;
            setTitle("Editar Nota");
            loadNoteData();
        } else {
            isEditMode = false;
            setTitle("Nueva Nota");
            btnDelete.setVisibility(View.GONE);
            createdAt = getCurrentDateTime();
        }
    }

    private void loadNoteData() {
        noteId = getIntent().getIntExtra("NOTE_ID", 0);
        String title = getIntent().getStringExtra("NOTE_TITLE");
        String content = getIntent().getStringExtra("NOTE_CONTENT");
        createdAt = getIntent().getStringExtra("CREATED_AT");

        etNoteTitle.setText(title);
        etNoteContent.setText(content);
    }

    private void setSelectedCategory() {
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", 0);
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCategoryId() == categoryId) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveNote());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void showAddCategoryDialog() {
        EditText input = new EditText(this);
        input.setHint("Nombre de la categoría");

        new AlertDialog.Builder(this)
                .setTitle("Nueva Categoría")
                .setView(input)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String categoryName = input.getText().toString().trim();
                    if (!categoryName.isEmpty()) {
                        addCategory(categoryName);
                    } else {
                        Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void addCategory(String categoryName) {
        Category category = new Category(categoryName);
        categoryController.insertCategory(category, new CategoryController.CategoryCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddEditNoteActivity.this,
                        "Categoría agregada",
                        Toast.LENGTH_SHORT).show();
                loadCategories();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditNoteActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNote() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categories == null || categories.isEmpty()) {
            Toast.makeText(this, "Debes crear al menos una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        int categoryId = categories.get(selectedPosition).getCategoryId();

        Note note = new Note(title, content, createdAt, categoryId);

        if (isEditMode) {
            note.setNoteId(noteId);
            updateNote(note);
        } else {
            insertNote(note);
        }
    }

    private void insertNote(Note note) {
        noteController.insertNote(note, new NoteController.NoteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddEditNoteActivity.this,
                        "Nota agregada",
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditNoteActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNote(Note note) {
        noteController.updateNote(note, new NoteController.NoteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddEditNoteActivity.this,
                        "Nota actualizada",
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditNoteActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar nota")
                .setMessage("¿Estás seguro?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteNote())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteNote() {
        int categoryId = categories.get(spinnerCategory.getSelectedItemPosition()).getCategoryId();
        Note note = new Note(
                etNoteTitle.getText().toString(),
                etNoteContent.getText().toString(),
                createdAt,
                categoryId
        );
        note.setNoteId(noteId);

        noteController.deleteNote(note, new NoteController.NoteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddEditNoteActivity.this,
                        "Nota eliminada",
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditNoteActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}