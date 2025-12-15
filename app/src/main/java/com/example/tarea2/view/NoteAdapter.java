package com.example.tarea2.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea2.R;
import com.example.tarea2.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private CategoryAdapter.OnNoteClickListener listener;

    public NoteAdapter(CategoryAdapter.OnNoteClickListener listener) {
        this.notes = new ArrayList<>();
        this.listener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNoteTitle;
        private final TextView tvNoteContent;
        private final TextView tvCreatedAt;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoteTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvNoteContent = itemView.findViewById(R.id.tvNoteContent);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        }

        public void bind(Note note) {
            tvNoteTitle.setText(note.getNoteTitle());
            tvNoteContent.setText(note.getNoteContent());
            tvCreatedAt.setText(note.getCreatedAt());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoteClick(
                            note.getNoteId(),
                            note.getNoteTitle(),
                            note.getNoteContent(),
                            note.getCreatedAt(),
                            note.getCategoryId()
                    );
                }
            });
        }
    }
}