package com.example.tarea2.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea2.R;
import com.example.tarea2.controller.CategoryController;
import com.example.tarea2.controller.NoteController;
import com.example.tarea2.model.Category;
import com.example.tarea2.model.CategoryWithNotes;
import com.example.tarea2.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnNoteClickListener {

    private static final int REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private CategoryController categoryController;
    private NoteController noteController;
    private FloatingActionButton fabAddNote;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initControllers();
        setupRecyclerView();
        setupSearch();
        loadCategoriesWithNotes();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewCategories);
        fabAddNote = findViewById(R.id.fabAddNote);
        etSearch = findViewById(R.id.etSearch);

        fabAddNote.setOnClickListener(v -> openAddEditActivity(null));
    }

    private void initControllers() {
        categoryController = new CategoryController(this);
        noteController = new NoteController(this);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                if (searchText.isEmpty()) {
                    loadCategoriesWithNotes();
                } else {
                    searchNotes(searchText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadCategoriesWithNotes() {
        categoryController.getCategoriesWithNotes(new CategoryController.CategoryWithNotesCallback() {
            @Override
            public void onSuccess(List<CategoryWithNotes> categoriesWithNotes) {
                categoryAdapter.setCategoriesWithNotes(categoriesWithNotes);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchNotes(String searchText) {
        noteController.searchNotes(searchText, new NoteController.NoteListCallback() {
            @Override
            public void onSuccess(List<Note> notes) {
                if (notes.isEmpty()) {
                    categoryAdapter.setCategoriesWithNotes(new ArrayList<>());
                    return;
                }

                CategoryWithNotes searchCategory = new CategoryWithNotes();
                Category tempCategory = new Category("Resultados de búsqueda");
                tempCategory.setCategoryId(0);
                searchCategory.category = tempCategory;
                searchCategory.notes = notes;

                List<CategoryWithNotes> searchResults = new ArrayList<>();
                searchResults.add(searchCategory);
                categoryAdapter.setCategoriesWithNotes(searchResults);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddEditActivity(Intent intent) {
        if (intent == null) {
            intent = new Intent(this, AddEditNoteActivity.class);
        }
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onNoteClick(int noteId, String noteTitle, String noteContent, String createdAt, int categoryId) {
        Intent intent = new Intent(this, AddEditNoteActivity.class);
        intent.putExtra("NOTE_ID", noteId);
        intent.putExtra("NOTE_TITLE", noteTitle);
        intent.putExtra("NOTE_CONTENT", noteContent);
        intent.putExtra("CREATED_AT", createdAt);
        intent.putExtra("CATEGORY_ID", categoryId);
        openAddEditActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            etSearch.setText("");
            loadCategoriesWithNotes();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}