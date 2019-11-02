package com.example.mitrip.historypackage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mitrip.R;
import com.example.mitrip.activity.Trip;
import com.example.mitrip.activity.TripActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private HistoryListAdapter tripAdapter;
    private RecyclerView recyclerView;

    DatabaseReference databaseTrips;

    ArrayList<Trip> historyTripList;
    View view;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recycler);

        databaseTrips = FirebaseDatabase.getInstance().getReference("trips");
        historyTripList = new ArrayList<>();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseTrips.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                historyTripList.clear();

                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    Trip historyTrip = tripSnapshot.getValue(Trip.class);

                    if (historyTrip.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        if (!(historyTrip.getTripStatus().equals(TripActivity.UPCOMING))){
                            historyTripList.add(historyTrip);
                        }
                    }

                    setAdapter();
                }

                if (historyTripList.size() <= 0){
                    view.findViewById(R.id.linearLayoutFragmentHistory).setVisibility(View.VISIBLE);
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        tripAdapter = new HistoryListAdapter(getContext());
        recyclerView.setAdapter(tripAdapter);
        setDataSource();

    }
    private void setDataSource() {
        tripAdapter.setDataToAdapter(historyTripList);
    }
}
