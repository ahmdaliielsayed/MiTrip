package com.mi.mitrip.MapPackage;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.mi.mitrip.R;
import com.mi.mitrip.activity.Trip;
import com.mi.mitrip.activity.TripActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    DatabaseReference databaseTrips;
    ArrayList<Trip> historyTrips;
    ArrayList<Integer> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        databaseTrips = FirebaseDatabase.getInstance().getReference("trips");
        historyTrips = new ArrayList<>();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng src1 = new LatLng(31.041455, 31.4178593);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(src1, 4f));
        colorList = new ArrayList<>();
        colorList.add(Color.RED);
        colorList.add(Color.GREEN);
        colorList.add(Color.YELLOW);
        colorList.add(Color.BLUE);
        colorList.add(Color.WHITE);
        colorList.add(Color.GRAY);
        colorList.add(Color.BLACK);
        colorList.add(Color.DKGRAY);
        colorList.add(Color.LTGRAY);

        new Thread(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                databaseTrips.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        historyTrips.clear();

                        for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                            Trip upcomingTrip = tripSnapshot.getValue(Trip.class);

                            if (upcomingTrip.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                if (upcomingTrip.getTripStatus().equals(TripActivity.CANCELLED) ||
                                        upcomingTrip.getTripStatus().equals(TripActivity.DONE)) {
                                    historyTrips.add(upcomingTrip);
                                }
                            }
                        }

                        //if i have a list of object
                        for (; i < historyTrips.size(); i++) {
                            Log.i("I", "i= " + i);
                            Trip trip = historyTrips.get(i);
                            double srclong = trip.getTripStartPointLongitude();
                            double scrlat = trip.getTripStartPointLatitude();
                            final LatLng src = new LatLng(srclong, scrlat);
                            mMap.addMarker(new MarkerOptions().position(src).title(trip.getTripName()));
                            double deslong = trip.getTripEndPointLongitude();
                            double deslat = trip.getTripEndPointLatitude();
                            final LatLng des = new LatLng(deslong, deslat);
                            mMap.addMarker(new MarkerOptions().position(des).title(trip.getTripName()));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (colorList.size() > i)
                                        mMap.addPolyline(new PolylineOptions().add(src).add(des).width(10f).color(colorList.get(i)));
                                    else {
                                        //i = 0;
                                        mMap.addPolyline(new PolylineOptions().add(src).add(des).width(10f).color(colorList.get(3)));
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        }).start();
    }
}
