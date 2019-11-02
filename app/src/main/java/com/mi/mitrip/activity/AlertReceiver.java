package com.mi.mitrip.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String TripID = intent.getStringExtra("TripID");
        String TripStartPoint = intent.getStringExtra("TripStartPoint");
        double TripStartPointLongitude = intent.getExtras().getDouble("TripStartPointLongitude");
        double TripStartPointLatitude = intent.getExtras().getDouble("TripStartPointLatitude");
        String TripEndPoint = intent.getStringExtra("TripEndPoint");
        double TripEndPointLongitude = intent.getExtras().getDouble("TripEndPointLongitude");
        double TripEndPointLatitude = intent.getExtras().getDouble("TripEndPointLatitude");
        String TripName = intent.getStringExtra("TripName");
        String TripDate = intent.getStringExtra("TripDate");
        String TripTime = intent.getStringExtra("TripTime");
        String TripStatus = intent.getStringExtra("TripStatus");
        String UserID = intent.getStringExtra("UserID");
        long calendar = intent.getExtras().getLong("calendar");

        Intent startIntent = new Intent(context, DialogueActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startIntent.putExtra("TripID", TripID);
        startIntent.putExtra("TripStartPoint", TripStartPoint);
        startIntent.putExtra("TripStartPointLongitude", TripStartPointLongitude);
        startIntent.putExtra("TripStartPointLatitude", TripStartPointLatitude);
        startIntent.putExtra("TripEndPoint", TripEndPoint);
        startIntent.putExtra("TripEndPointLongitude", TripEndPointLongitude);
        startIntent.putExtra("TripEndPointLatitude", TripEndPointLatitude);
        startIntent.putExtra("TripName", TripName);
        startIntent.putExtra("TripDate", TripDate);
        startIntent.putExtra("TripTime", TripTime);
        startIntent.putExtra("TripStatus", TripStatus);
        startIntent.putExtra("UserID", UserID);
        startIntent.putExtra("calendar", calendar);
        context.startActivity(startIntent);
    }
}
