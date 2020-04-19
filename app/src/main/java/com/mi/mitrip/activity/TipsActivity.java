package com.mi.mitrip.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;

import com.mi.mitrip.R;

public class TipsActivity extends AppCompatActivity {

    Button btnDoItNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        btnDoItNow = findViewById(R.id.btnDoItNow);

        btnDoItNow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("BatteryLife")
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                        Intent intent = new Intent();
                        intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
