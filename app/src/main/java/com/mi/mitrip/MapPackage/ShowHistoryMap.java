package com.mi.mitrip.MapPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;

import android.os.Bundle;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mi.mitrip.MapPackage.map.DirectionFinder;
import com.mi.mitrip.MapPackage.map.DirectionFinderListener;
import com.mi.mitrip.MapPackage.map.Route;
import com.mi.mitrip.R;
import com.mi.mitrip.activity.Trip;
import com.mi.mitrip.activity.TripActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.mi.mitrip.MapPackage.map.GoogleMapHelper.buildCameraUpdate;
import static com.mi.mitrip.MapPackage.map.GoogleMapHelper.defaultMapSettings;
import static com.mi.mitrip.MapPackage.map.GoogleMapHelper.getDefaultPolyLines;
import static com.mi.mitrip.MapPackage.map.GoogleMapHelper.getDottedPolylines;
import static com.mi.mitrip.MapPackage.map.UiHelper.showAlwaysCircularProgressDialog;


public class ShowHistoryMap extends AppCompatActivity implements DirectionFinderListener {

    DatabaseReference databaseTrips;

    ArrayList<Trip> historyTripList;

    private enum PolylineStyle {
        DOTTED,
        PLAIN
    }

    private static final String[] POLYLINE_STYLE_OPTIONS = new String[]{
            "PLAIN",
            "DOTTED"
    };
    private PolylineStyle polylineStyle = PolylineStyle.PLAIN;
    private GoogleMap googleMap1;
    private Polyline polyline;
    private MaterialDialog materialDialog;
    private Toolbar toolbar;
//    private TripViewModel tripViewModel;
    private FirebaseAuth mAuth;
    private LiveData<Trip> history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        databaseTrips = FirebaseDatabase.getInstance().getReference("trips");
        historyTripList = new ArrayList<>();

//        mAuth =FirebaseAuth.getInstance();
//        toolbar=findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
//        toolbar.setTitleTextColor(Color.WHITE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                defaultMapSettings(googleMap);
                ShowHistoryMap.this.googleMap1 = googleMap;
            }
        });
        String fromFab = getIntent().getStringExtra("key");
//        tripViewModel= ViewModelProviders.of(this).get(TripViewModel.class);
//        tripViewModel.setContext(this);

        if(fromFab.equals("allHistoryTrips")) {

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

                    }

                    if (historyTripList.size() <= 0){
//                    view.findViewById(R.id.linearLayoutFragmentHistory).setVisibility(View.VISIBLE);
                        Toast.makeText(ShowHistoryMap.this, "Empty History!", Toast.LENGTH_SHORT).show();
                    }

                    for (Trip t : historyTripList) {
                        if (historyTripList.size() <= 0){
//                    view.findViewById(R.id.linearLayoutFragmentHistory).setVisibility(View.VISIBLE);
                            Toast.makeText(ShowHistoryMap.this, "Empty History!", Toast.LENGTH_SHORT).show();
                        }
                        String o = "";
                        o = o + "" + t.getTripStartPointLongitude() + "," + t.getTripStartPointLatitude();
                        String d = "";
                        d = d + "" + t.getTripEndPointLongitude() + "," + t.getTripEndPointLatitude();
                        System.out.println(o + "," + d);
                        if (!o.isEmpty() && !d.isEmpty())
                            fetchDirections(o, d);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            String source = getIntent().getStringExtra("source");
            String destination = getIntent().getStringExtra("destination");
            System.out.println("source: " + source + "destination: " + destination);
            fetchDirections(source, destination);

//            fetchDirections("31.040949,31.378469", "30.044420,31.235712");
//            fetchDirections("31.040949,31.378469", "31.200092,29.918739");
        }
    }
    private void fetchDirections(String origin, String destination) {
        try {
            new DirectionFinder(this, origin, destination).execute(); // 1
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDirectionFinderStart() {
        if(!this.isFinishing()&&!this.isDestroyed()) {
            if (materialDialog == null)
                materialDialog = showAlwaysCircularProgressDialog(this, getText(R.string.fetchDirection).toString());
            else materialDialog.show();
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        //if (!routes.isEmpty() && polyline != null) polyline.remove();
        System.out.println("for (Route route : routes) "+routes);
        try {
            for (Route route : routes) {
                System.out.println("for (Route route : routes) "+route);
                PolylineOptions polylineOptions = getDefaultPolyLines(route.points);
                if (polylineStyle == PolylineStyle.DOTTED)
                    polylineOptions = getDottedPolylines(route.points);
                polyline = googleMap1.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error occurred on finding the directions...", Toast.LENGTH_SHORT).show();
        }
        if(routes.size()>0) {
            googleMap1.setMinZoomPreference(2.0f);
            googleMap1.setMaxZoomPreference(20.0f);
            googleMap1.animateCamera(buildCameraUpdate(routes.get(0).endLocation),3, null);

        }
    }
}