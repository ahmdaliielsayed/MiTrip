package com.example.mitrip.AddNotePackage.ViewPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mitrip.AddNotePackage.ModelPackage.Notes;
import com.example.mitrip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddNoteActivity extends AppCompatActivity {

    ImageButton btnAddPlus;
    EditText editTxtAddNote;

    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private ArrayList<Notes> notesArrayList;

    DatabaseReference databaseNotes;
    String TripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initComponents();
    }

    private void initComponents() {
        TripID = getIntent().getStringExtra("tripID");

        databaseNotes = FirebaseDatabase.getInstance().getReference("notes");
        databaseNotes.keepSynced(true);

        editTxtAddNote = findViewById(R.id.editTxtAddNote);

        btnAddPlus = findViewById(R.id.btnAddPlus);
        btnAddPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTxtAddNote.getText().toString().trim().equals("")){
                    editTxtAddNote.setError(getText(R.string.emptyNote));
                    editTxtAddNote.requestFocus();
                } else {
                    String noteID = databaseNotes.push().getKey();

                    Notes note = new Notes(noteID, editTxtAddNote.getText().toString().trim(), TripID);
                    databaseNotes.child(noteID).setValue(note);

                    editTxtAddNote.setText("");

                    Toast.makeText(AddNoteActivity.this, R.string.noteAddedSuccessfully, Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        notesArrayList = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseNotes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                notesArrayList.clear();

                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Notes note = noteSnapshot.getValue(Notes.class);

                    if (note.getTripID().equals(TripID)){
                        notesArrayList.add(note);
                    }

                    setAdapter();
                }

                if (notesArrayList.size() <= 0){
                    findViewById(R.id.linearLayoutFragmentUpcoming).setVisibility(View.VISIBLE);
                    return;
                } else {
                    findViewById(R.id.linearLayoutFragmentUpcoming).setVisibility(View.GONE);
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        notesAdapter = new NotesAdapter(this);
        recyclerView.setAdapter(notesAdapter);
        setDataSource();

    }
    private void setDataSource() {
        notesAdapter.setDataToAdapter(notesArrayList);
    }
}
