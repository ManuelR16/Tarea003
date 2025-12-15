package com.example.tarea2.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea2.R;
import com.example.tarea2.controller.HistoryController;
import com.example.tarea2.model.History;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private HistoryController historyController;
    private Spinner spinnerFilter;
    private EditText etDateFilter;

    private String[] filterOptions = {
            "Todos",
            "Notas agregadas",
            "Notas actualizadas",
            "Notas eliminadas",
            "Categorías agregadas",
            "Categorías actualizadas",
            "Categorías eliminadas",
            "Filtrar por fecha"
    };

    private String[] actionTypes = {
            "",
            "insert_note",
            "update_note",
            "delete_note",
            "insert_category",
            "update_category",
            "delete_category",
            ""
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setTitle("Historial de Acciones");

        initViews();
        initController();
        setupRecyclerView();
        setupFilter();
        loadAllHistory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewHistory);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        etDateFilter = findViewById(R.id.etDateFilter);
    }

    private void initController() {
        historyController = new HistoryController(this);
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyAdapter);
    }

    private void setupFilter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filterOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    etDateFilter.setVisibility(View.GONE);
                    loadAllHistory();
                } else if (position == filterOptions.length - 1) {
                    etDateFilter.setVisibility(View.VISIBLE);
                    etDateFilter.setHint("dd/MM/yyyy");
                } else {
                    etDateFilter.setVisibility(View.GONE);
                    filterByAction(actionTypes[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etDateFilter.setOnEditorActionListener((v, actionId, event) -> {
            String date = etDateFilter.getText().toString().trim();
            if (!date.isEmpty()) {
                filterByDate(date);
            }
            return true;
        });
    }

    private void loadAllHistory() {
        historyController.getAllHistory(new HistoryController.HistoryListCallback() {
            @Override
            public void onSuccess(List<History> historyList) {
                historyAdapter.setHistoryList(historyList);
                if (historyList.isEmpty()) {
                    Toast.makeText(HistoryActivity.this,
                            "No hay historial",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HistoryActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByAction(String actionType) {
        historyController.getHistoryByAction(actionType, new HistoryController.HistoryListCallback() {
            @Override
            public void onSuccess(List<History> historyList) {
                historyAdapter.setHistoryList(historyList);
                if (historyList.isEmpty()) {
                    Toast.makeText(HistoryActivity.this,
                            "No hay registros de este tipo",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HistoryActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByDate(String date) {
        historyController.getHistoryByDate(date, new HistoryController.HistoryListCallback() {
            @Override
            public void onSuccess(List<History> historyList) {
                historyAdapter.setHistoryList(historyList);
                if (historyList.isEmpty()) {
                    Toast.makeText(HistoryActivity.this,
                            "No hay registros en esta fecha",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HistoryActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}