package com.example.mitrip.upcomingpackage;


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
public class UpcomingFragment extends Fragment {

    private UpcomingListAdapter tripAdapter;
    private RecyclerView recyclerView;

    DatabaseReference databaseTrips;

    ArrayList<Trip> upcomingTripList;

    View view;

    public UpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        recyclerView = view.findViewById(R.id.recycler);

        databaseTrips = FirebaseDatabase.getInstance().getReference("trips");
        databaseTrips.keepSynced(true);
        upcomingTripList = new ArrayList<>();

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

                upcomingTripList.clear();

                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    Trip upcomingTrip = tripSnapshot.getValue(Trip.class);

                    if (upcomingTrip.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        if (upcomingTrip.getTripStatus().equals(TripActivity.UPCOMING)){
                            upcomingTripList.add(upcomingTrip);
                        }
                    }

                    setAdapter();
                }

                if (upcomingTripList.size() <= 0){
                    view.findViewById(R.id.linearLayoutFragmentUpcoming).setVisibility(View.VISIBLE);
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
        tripAdapter = new UpcomingListAdapter(getActivity());
        recyclerView.setAdapter(tripAdapter);
        setDataSource();

    }
    private void setDataSource() {
        tripAdapter.setDataToAdapter(upcomingTripList);
    }
}
