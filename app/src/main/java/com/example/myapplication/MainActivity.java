package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.data.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String userID;
    private ImageView UserImage;
    private TextView UserName;
    private TextView UserEmail;
    private Button AddNote;
    private LinearLayoutCompat new_note_input_view;
    private Button save_new_note;
    private Button cancle_new_note;
    private TextInputEditText title_input;
    private TextInputEditText body_input;
    private RecyclerView note_list;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private Button uploadImageButton;
    private ImageView noteImageView;
    private Uri imageUri; // To store the image URI


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();
        addNewNote();
        saveNewNote();
        cancleNewNote();
    }

    private void initViews() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(this).load(user.getPhotoUrl()).centerCrop().placeholder(R.drawable.logo).into(UserImage);
        UserName.setText(user.getDisplayName());
        UserEmail.setText(user.getEmail());
        userID = user.getEmail();

        // Initialize RecyclerView and Adapter
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList);
        note_list.setLayoutManager(new LinearLayoutManager(this));
        note_list.setAdapter(noteAdapter);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("notes");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear(); // Clear the old data
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    if(Objects.equals(note.getUserID(), userID)){
                        noteList.add(note); // Add each note to the list if the userID match
                    }
                }
                noteAdapter.notifyDataSetChanged(); // Notify adapter to update the UI
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("MainActivity", "Failed to read value.", error.toException());
            }
        });
    }

    private void saveToDB(String title, String body, String imageUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("notes").push();
        String noteId = myRef.getKey(); // Get the generated key
        Note note = new Note(noteId, title, body, userID, imageUrl); // Use the key in the note
        myRef.setValue(note);
    }

    private void addNewNote() {
        AddNote.setOnClickListener(v -> {
            new_note_input_view.setVisibility(View.VISIBLE);

            uploadImageButton.setOnClickListener(v1 -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            noteImageView.setVisibility(View.VISIBLE);
            noteImageView.setImageURI(imageUri);
        }
    }


    private void saveNewNote() {
        save_new_note.setOnClickListener(v -> {
            String title = title_input.getText().toString();
            String body = body_input.getText().toString();
            title_input.setText("");
            body_input.setText("");
            new_note_input_view.setVisibility(View.GONE);

            if (imageUri != null) {
                uploadImageToFirebase(title, body);
            } else {
                saveToDB(title, body, null); // Save without image
            }
        });
    }

    private void cancleNewNote(){
        cancle_new_note.setOnClickListener(v -> {
            title_input.setText("");
            body_input.setText("");
            new_note_input_view.setVisibility(View.GONE);
        });
    }

    private void uploadImageToFirebase(String title, String body) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");

        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToDB(title, body, uri.toString());
                })
        ).addOnFailureListener(e -> {
            // Handle failed upload
        });
    }

    private void findViews() {
        UserImage = findViewById(R.id.UserImage);
        UserName = findViewById(R.id.UserName);
        UserEmail = findViewById(R.id.UserEmail);
        note_list = findViewById(R.id.note_list); // Initialize RecyclerView
        AddNote = findViewById(R.id.AddNote);
        new_note_input_view = findViewById(R.id.new_note_input_view);
        save_new_note = findViewById(R.id.save_new_note);
        cancle_new_note = findViewById(R.id.cancle_new_note);
        title_input = findViewById(R.id.title_input);
        body_input = findViewById(R.id.body_input);
        uploadImageButton = findViewById(R.id.upload_image_button);
        noteImageView = findViewById(R.id.note_image_view);
    }
}
