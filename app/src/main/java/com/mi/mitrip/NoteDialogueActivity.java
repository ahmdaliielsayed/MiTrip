package com.mi.mitrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mi.mitrip.AddNotePackage.ModelPackage.Notes;
import com.mi.mitrip.AddNotePackage.ViewPackage.NotesAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoteDialogueActivity extends AppCompatActivity {

    TextView txtViewTripName;

    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private ArrayList<Notes> notesArrayList;

    DatabaseReference databaseNotes;

    String TripID, TripName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_dialogue);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        initComponents();
    }

    private void initComponents() {
        TripID = getIntent().getStringExtra("tripID");
        TripName = getIntent().getStringExtra("tripName");

        databaseNotes = FirebaseDatabase.getInstance().getReference("notes");

        txtViewTripName = findViewById(R.id.txtViewTripName);
        txtViewTripName.setText(TripName);
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
