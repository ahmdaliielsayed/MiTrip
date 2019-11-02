package com.example.mitrip.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mitrip.FloatingWidgetShowService;
import com.example.mitrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DialogueActivity extends AppCompatActivity {

    MediaPlayer mediaPlayerSong;
    TextView txtViewTripName, txtViewStartPoint, txtViewEndPoint;

    DatabaseReference databaseTrips;
    Button btnStartTrip, btnCancelTrip, btnLaterTrip;

    String TripID, TripStartPoint, TripEndPoint, TripName, TripDate, TripTime, TripStatus, UserID;
    double TripStartPointLongitude, TripStartPointLatitude, TripEndPointLongitude, TripEndPointLatitude;
    long calendar;

    public static final int SYSTEM_ALERT_WINDOW_PERMISSION = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogue);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(false);

        initComponents();
    }

    private void initComponents() {
        databaseTrips = FirebaseDatabase.getInstance().getReference("trips");

        TripID = getIntent().getStringExtra("TripID");
        TripStartPoint = getIntent().getStringExtra("TripStartPoint");
        TripStartPointLongitude = getIntent().getExtras().getDouble("TripStartPointLongitude");
        TripStartPointLatitude = getIntent().getExtras().getDouble("TripStartPointLatitude");
        TripEndPoint = getIntent().getStringExtra("TripEndPoint");
        TripEndPointLongitude = getIntent().getExtras().getDouble("TripEndPointLongitude");
        TripEndPointLatitude = getIntent().getExtras().getDouble("TripEndPointLatitude");
        TripName = getIntent().getStringExtra("TripName");
        TripDate = getIntent().getStringExtra("TripDate");
        TripTime = getIntent().getStringExtra("TripTime");
        TripStatus = getIntent().getStringExtra("TripStatus");
        UserID = getIntent().getStringExtra("UserID");
        calendar = getIntent().getExtras().getLong("calendar",0);

        txtViewTripName = findViewById(R.id.txtViewTripName);
        txtViewTripName.setText(TripName);

        txtViewStartPoint = findViewById(R.id.txtViewStartPoint);
        txtViewStartPoint.setText(TripStartPoint);

        txtViewEndPoint = findViewById(R.id.txtViewEndPoint);
        txtViewEndPoint.setText(TripEndPoint);

        btnStartTrip = findViewById(R.id.btnStartTrip);
        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayerSong.stop();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(DialogueActivity.this)) {
                    new androidx.appcompat.app.AlertDialog.Builder(DialogueActivity.this)
                            .setTitle(R.string.warning)
                            .setMessage(R.string.errorMsgPermissionRequired)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RuntimePermissionForUser();
                                }
                            })
                            .setIcon(R.drawable.warning)
                            .show();
                    return;
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Intent intent = new Intent (DialogueActivity.this, FloatingWidgetShowService.class);
                    intent.putExtra("tripID", TripID);
                    intent.putExtra("tripName", TripName);
                    startService(intent);
                    finish();
                } else if (Settings.canDrawOverlays(DialogueActivity.this)) {
                    Intent intent = new Intent (DialogueActivity.this, FloatingWidgetShowService.class);
                    intent.putExtra("tripID", TripID);
                    intent.putExtra("tripName", TripName);
                    startService(intent);
                    finish();
                } else {
                    new androidx.appcompat.app.AlertDialog.Builder(DialogueActivity.this)
                            .setTitle(R.string.warning)
                            .setMessage(R.string.errorMsgPermissionRequired)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RuntimePermissionForUser();
                                }
                            })
                            .setIcon(R.drawable.warning)
                            .show();
                    return;
                }

//                String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + TripStartPointLongitude + "," + TripStartPointLatitude + "&daddr=" + TripEndPointLongitude + "," + TripEndPointLatitude;
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                startActivity(Intent.createChooser(intent, getText(R.string.selectApplication)));

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + TripEndPoint);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(Intent.createChooser(mapIntent, getText(R.string.selectApplication)));

                Trip tripDone = new Trip(TripID, TripStartPoint, TripStartPointLongitude, TripStartPointLatitude, TripEndPoint, TripEndPointLongitude, TripEndPointLatitude, TripName, TripDate, TripTime, TripActivity.DONE, FirebaseAuth.getInstance().getCurrentUser().getUid(), calendar);
                // store this trip to firebase
                databaseTrips.child(TripID).setValue(tripDone);
                finishAffinity();
            }
        });

        btnCancelTrip = findViewById(R.id.btnCancelTrip);
        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayerSong.stop();

                Trip tripCancel = new Trip(TripID, TripStartPoint, TripStartPointLongitude, TripStartPointLatitude, TripEndPoint, TripEndPointLongitude, TripEndPointLatitude, TripName, TripDate, TripTime, TripActivity.CANCELLED, FirebaseAuth.getInstance().getCurrentUser().getUid(), calendar);
                // store this trip to firebase
                databaseTrips.child(TripID).setValue(tripCancel);
                finish();
            }
        });

        btnLaterTrip = findViewById(R.id.btnLaterTrip);
        btnLaterTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayerSong.stop();

                openNotification();
                finish();
            }
        });

        mediaPlayerSong = MediaPlayer.create(this, R.raw.cucko);
        mediaPlayerSong.start();
    }

    public void openNotification() {
        Notification notificationHelper = new Notification(this, TripID, TripStartPoint, TripStartPointLongitude, TripStartPointLatitude, TripEndPoint, TripEndPointLongitude, TripEndPointLatitude, TripName, TripDate, TripTime, TripActivity.CANCELLED, FirebaseAuth.getInstance().getCurrentUser().getUid());
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(TripID.hashCode(), nb.build());
    }

    public void RuntimePermissionForUser() {

        Intent PermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(PermissionIntent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }
}
