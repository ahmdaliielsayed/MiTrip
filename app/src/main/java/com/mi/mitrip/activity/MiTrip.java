package com.mi.mitrip.activity;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MiTrip extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
