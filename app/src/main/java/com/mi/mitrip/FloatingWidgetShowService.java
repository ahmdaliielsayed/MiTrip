package com.mi.mitrip;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatingWidgetShowService extends Service {

    WindowManager windowManager;
    View floatingView, collapsedView;

    WindowManager.LayoutParams params ;

    String TripID, TripName;

    public FloatingWidgetShowService() { }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TripID = intent.getStringExtra("tripID");
        TripName = intent.getStringExtra("tripName");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //getting the widget layout from xml using layout inflater
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null);

        //setting the layout parameters
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //getting windows services and adding the floating view to it
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        //getting the collapsed and expanded view from the floating view
//        expandedView = floatingView.findViewById(R.id.Layout_Expended);
        collapsedView = floatingView.findViewById(R.id.Layout_Collapsed);

        //adding click listener to close button and expanded view
        floatingView.findViewById(R.id.Widget_Close_Icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });

        //adding an touchlistener to make drag movement of the floating widget
        floatingView.findViewById(R.id.MainParentRelativeLayout).setOnTouchListener(new View.OnTouchListener() {
            int X_Axis, Y_Axis;
            float TouchX, TouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        X_Axis = params.x;
                        Y_Axis = params.y;
                        TouchX = event.getRawX();
                        TouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:

                        float xDiff = event.getRawX() - TouchX;
                        float yDiff = event.getRawY() - TouchY;

                        if ((Math.abs(xDiff) < 5) && (Math.abs(yDiff) < 5)) {
                            Intent openNoteDialogue = new Intent(getApplicationContext(), NoteDialogueActivity.class);
                            openNoteDialogue.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            openNoteDialogue.putExtra("tripID", TripID);
                            openNoteDialogue.putExtra("tripName", TripName);
                            getApplicationContext().startActivity(openNoteDialogue);
                            return true;
                        }

                    case MotionEvent.ACTION_MOVE:

                        params.x = X_Axis + (int) (event.getRawX() - TouchX);
                        params.y = Y_Axis + (int) (event.getRawY() - TouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}