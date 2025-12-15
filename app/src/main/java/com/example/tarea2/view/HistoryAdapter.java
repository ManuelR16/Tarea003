package com.example.tarea2.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea2.R;
import com.example.tarea2.model.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> historyList;

    public HistoryAdapter() {
        this.historyList = new ArrayList<>();
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAction;
        private final TextView tvDetails;
        private final TextView tvCreatedAt;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        }

        public void bind(History history) {
            tvAction.setText(getActionText(history.getAction()));
            tvDetails.setText(history.getDetails());
            tvCreatedAt.setText(history.getCreatedAt());
        }

        private String getActionText(String action) {
            switch (action) {
                case "insert_note":
                    return "+ Nota agregada";
                case "update_note":
                    return "Nota actualizada";
                case "delete_note":
                    return "x Nota eliminada";
                case "insert_category":
                    return "+ Categoría agregada";
                case "update_category":
                    return "Categoría actualizada";
                case "delete_category":
                    return "x Categoría eliminada";
                default:
                    return action;
            }
        }
    }
}