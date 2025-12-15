package com.example.tarea2.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea2.R;
import com.example.tarea2.model.CategoryWithNotes;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryWithNotes> categoriesWithNotes;
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(int noteId, String noteTitle, String noteContent, String createdAt, int categoryId);
    }

    public CategoryAdapter(OnNoteClickListener listener) {
        this.categoriesWithNotes = new ArrayList<>();
        this.listener = listener;
    }

    public void setCategoriesWithNotes(List<CategoryWithNotes> categoriesWithNotes) {
        this.categoriesWithNotes = categoriesWithNotes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryWithNotes categoryWithNotes = categoriesWithNotes.get(position);
        holder.bind(categoryWithNotes);
    }

    @Override
    public int getItemCount() {
        return categoriesWithNotes.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCategoryName;
        private final TextView tvNoteCount;
        private final RecyclerView rvNotes;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvNoteCount = itemView.findViewById(R.id.tvNoteCount);
            rvNotes = itemView.findViewById(R.id.rvNotes);
        }

        public void bind(CategoryWithNotes categoryWithNotes) {
            tvCategoryName.setText(categoryWithNotes.category.getCategoryName());
            int noteCount = categoryWithNotes.notes.size();
            tvNoteCount.setText(noteCount + " nota(s)");

            NoteAdapter noteAdapter = new NoteAdapter(listener);
            noteAdapter.setNotes(categoryWithNotes.notes);
            rvNotes.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvNotes.setAdapter(noteAdapter);
            rvNotes.setNestedScrollingEnabled(false);
        }
    }
}