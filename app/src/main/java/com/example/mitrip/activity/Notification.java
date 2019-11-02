package com.example.mitrip.activity;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mitrip.R;

public class Notification extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    public static int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);

    private NotificationManager mManager;
    String TripID, TripStartPoint, TripEndPoint, TripName, TripDate, TripTime, TripStatus, UserID;
    double TripStartPointLongitude, TripStartPointLatitude, TripEndPointLongitude, TripEndPointLatitude;

    public Notification(Context base, String TripID, String TripStartPoint, double TripStartPointLongitude, double TripStartPointLatitude, String TripEndPoint, double TripEndPointLongitude, double TripEndPointLatitude, String TripName, String TripDate, String TripTime, String  TripStatus, String UserID) {
        super(base);
        this.TripID = TripID;
        this.TripStartPoint = TripStartPoint;
        this.TripEndPoint = TripEndPoint;
        this.TripName = TripName;
        this.TripDate = TripDate;
        this.TripTime = TripTime;
        this.TripStatus = TripStatus;
        this.UserID = UserID;
        this.TripStartPointLongitude = TripStartPointLongitude;
        this.TripStartPointLatitude = TripStartPointLatitude;
        this.TripEndPointLongitude = TripEndPointLongitude;
        this.TripEndPointLatitude = TripEndPointLatitude;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {

        Intent intent = new Intent(getApplicationContext(), DialogueActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("TripID", TripID);
        intent.putExtra("TripStartPoint", TripStartPoint);
        intent.putExtra("TripStartPointLongitude", TripStartPointLongitude);
        intent.putExtra("TripStartPointLatitude", TripStartPointLatitude);
        intent.putExtra("TripEndPoint", TripEndPoint);
        intent.putExtra("TripEndPointLongitude", TripEndPointLongitude);
        intent.putExtra("TripEndPointLatitude", TripEndPointLatitude);
        intent.putExtra("TripName", TripName);
        intent.putExtra("TripDate", TripDate);
        intent.putExtra("TripTime", TripTime);
        intent.putExtra("TripStatus", TripStatus);
        intent.putExtra("UserID", UserID);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, intent, PendingIntent.FLAG_ONE_SHOT);

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(TripName)
                .setContentText(getText(R.string.openDialogue))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);
    }
}