package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;

    public NoteAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.noteTitle.setText(note.getTitle());
        holder.noteBody.setText(note.getBody());

        if (note.getImageUrl() != null && !note.getImageUrl().isEmpty()) {
            holder.noteImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(note.getImageUrl()).into(holder.noteImageView);
        } else {
            holder.noteImageView.setVisibility(View.GONE);
        }

        holder.noteTitle.setOnClickListener(v -> {
            boolean isVisible = holder.noteBody.getVisibility() == View.VISIBLE;
            holder.noteBody.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            holder.noteBody.setMaxLines(isVisible ? 0 : 30);
        });

        holder.deleteButton.setOnClickListener(v -> {
            deleteNoteFromDB(note);
            noteList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, noteList.size());
        });
    }

    private void deleteNoteFromDB(Note note) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference noteRef = database.getReference("notes").child(note.getId());
        noteRef.removeValue();
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle;
        TextView noteBody;
        ImageView noteImageView;  // Add this line
        Button deleteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteBody = itemView.findViewById(R.id.note_text_content);
            noteImageView = itemView.findViewById(R.id.note_image_view);  // Ensure this ID matches the XML ID
            deleteButton = itemView.findViewById(R.id.deleteBTN);
        }
    }


}
