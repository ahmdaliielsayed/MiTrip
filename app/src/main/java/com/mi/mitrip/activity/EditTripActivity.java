package com.mi.mitrip.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mi.mitrip.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTripActivity extends AppCompatActivity {

    private static final String TAG = "EditTripActivity";

    public static final String UPCOMING = "Upcoming";

    private PlacesClient placesClient;
    private String APIKey, startPoint, endPoint;
    private double startPointLongitude, startPointLatitude, endPointLongitude, endPointLatitude;
    private EditText editTxtTripName, editTxtDate1, editTxtTime1;
    private DatePickerDialog.OnDateSetListener calender1;
    private TimePickerDialog timePickerDialog;

    Date date, myDateCheck;
    private int mHour = 0, mMin = 0, hours1 = 0, min1 = 0, year1 = 0, month1 = 0, dayOfMonth1 = 0;

    private Button btnEdit;

    DatabaseReference databaseTrips;

    Calendar calendarAlarm1;

    String TripID, TripStatus, UserID;
    long calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        editTxtTripName = findViewById(R.id.editTxtTripName);
        editTxtDate1 = findViewById(R.id.editTxtDate1);
        editTxtTime1 = findViewById(R.id.editTxtTime1);

        btnEdit = findViewById(R.id.btnEdit);

        databaseTrips = FirebaseDatabase.getInstance().getReference("trips");

//        APIKey = "AIzaSyA23ck8mw7hzhKQ-uSLWVmBd2XbzB0PyK8";
        APIKey = "AIzaSyA23ck8mw7hzhKQ-uSLWVmBd2XbzB0PyK8";

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), APIKey);
        }
        placesClient = Places.createClient(this);

        /*** start point ***/
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
        // Specify the types of place data to return.
        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                startPoint = place.getName();
                startPointLatitude = place.getLatLng().longitude;
                startPointLongitude = place.getLatLng().latitude;
                Toast.makeText(EditTripActivity.this, place.getName(), Toast.LENGTH_LONG);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Toast.makeText(EditTripActivity.this, R.string.error, Toast.LENGTH_LONG);
            }
        });

        /*** end point ***/
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment2 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);
        // Specify the types of place data to return.
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                endPoint = place.getName();
                endPointLatitude = place.getLatLng().longitude;
                endPointLongitude = place.getLatLng().latitude;
                Toast.makeText(EditTripActivity.this, place.getName(), Toast.LENGTH_LONG);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Toast.makeText(EditTripActivity.this, R.string.error, Toast.LENGTH_LONG);
            }
        });

        /*** trip1 date ***/
        editTxtDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        EditTripActivity.this,
                        calender1,
                        year,
                        month,
                        day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
        calender1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;

                year1 = year;
                month1 = month;
                dayOfMonth1 = dayOfMonth;

                String startDate = dayOfMonth + "/" + month + "/" + year;
                String time = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                try {
                    date = format.parse(time);
                    myDateCheck = format.parse(startDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (myDateCheck.equals(null)) {
                    try {
                        myDateCheck = format.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (myDateCheck.before(date)) {
                    Toast.makeText(EditTripActivity.this, R.string.enterValidTripDate, Toast.LENGTH_LONG).show();
                } else {
                    editTxtDate1.setText(startDate);
                    editTxtTime1.setText("");
                }
            }
        };

        /*** trip1 time ***/
        editTxtTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMin = c.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(EditTripActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        Calendar myCalInstance = Calendar.getInstance();
                        Calendar myRealCalender = Calendar.getInstance();

                        if (myDateCheck == null) {
                            String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            try {
                                myDateCheck = format.parse(timeStamp);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        myRealCalender.setTime(myDateCheck);
                        myRealCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myRealCalender.set(Calendar.MINUTE, minutes);

                        if ((myRealCalender.getTime()).before(myCalInstance.getTime())) {
                            Toast.makeText(EditTripActivity.this, R.string.enterValidTripDate, Toast.LENGTH_LONG).show();
                        } else {
                            hours1 = hourOfDay;
                            min1 = minutes;
                            calendarAlarm1 = Calendar.getInstance();
//                            calendarAlarm1.setTime(date);
                            calendarAlarm1.set(Calendar.YEAR, year1);
                            calendarAlarm1.set(Calendar.MONTH, month1 - 1);
                            calendarAlarm1.set(Calendar.DAY_OF_MONTH, dayOfMonth1);
                            calendarAlarm1.set(Calendar.HOUR_OF_DAY, hours1);
                            calendarAlarm1.set(Calendar.MINUTE, min1);
                            calendarAlarm1.set(Calendar.SECOND, 0);
                            if (hourOfDay < 10 && minutes >= 10) {
                                editTxtTime1.setText("0" + hourOfDay + ":" + minutes);
                            } else if (hourOfDay < 10 && minutes < 10) {
                                editTxtTime1.setText("0" + hourOfDay + ":0" + minutes);
                            } else if (hourOfDay >= 10 && minutes < 10) {
                                editTxtTime1.setText(hourOfDay + ":0" + minutes);
                            } else if (hourOfDay >= 10 && minutes >= 10) {
                                editTxtTime1.setText(hourOfDay + ":" + minutes);
                            }
                        }
                    }
                }, mHour, mMin, false);
                timePickerDialog.show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editTrip();
            }
        });

        TripID = getIntent().getStringExtra("TripID");
        startPoint = getIntent().getStringExtra("TripStartPoint");
        autocompleteFragment1.setText(startPoint);
        startPointLongitude = getIntent().getExtras().getDouble("TripStartPointLongitude");
        startPointLatitude = getIntent().getExtras().getDouble("TripStartPointLatitude");
        endPoint = getIntent().getStringExtra("TripEndPoint");
        autocompleteFragment2.setText(endPoint);
        endPointLongitude = getIntent().getExtras().getDouble("TripEndPointLongitude");
        endPointLatitude = getIntent().getExtras().getDouble("TripEndPointLatitude");
        editTxtTripName.setText(getIntent().getStringExtra("TripName"));
        editTxtDate1.setText(getIntent().getStringExtra("TripDate"));
        editTxtTime1.setText(getIntent().getStringExtra("TripTime"));
        TripStatus = getIntent().getStringExtra("TripStatus");
        UserID = getIntent().getStringExtra("UserID");
        calendar = getIntent().getExtras().getLong("TripCalendar");
    }

    private void editTrip() {

        if (startPoint == null || startPoint == "") {
            new AlertDialog.Builder(EditTripActivity.this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.errorMsgStartPoint)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setIcon(R.drawable.cancel)
                    .show();
        } else if (endPoint == null || endPoint == "") {
            new AlertDialog.Builder(EditTripActivity.this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.errorMsgEndPoint)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setIcon(R.drawable.cancel)
                    .show();
        } else if (editTxtTripName.getText().toString().isEmpty()) {
            Toast.makeText(EditTripActivity.this, R.string.enterValidTripName, Toast.LENGTH_LONG).show();
            editTxtTripName.requestFocus();
        } else if (editTxtDate1.getText().toString().isEmpty()) {
            Toast.makeText(EditTripActivity.this, R.string.enterValidTripDate, Toast.LENGTH_LONG).show();
        } else if (editTxtTime1.getText().toString().isEmpty()) {
            Toast.makeText(EditTripActivity.this, R.string.enterValidTripTime, Toast.LENGTH_LONG).show();
            return;
        } else {

            if (calendarAlarm1 == null){
                Trip trip = new Trip(TripID, startPoint, startPointLongitude, startPointLatitude, endPoint, endPointLongitude, endPointLatitude, editTxtTripName.getText().toString(), editTxtDate1.getText().toString(), editTxtTime1.getText().toString(), UPCOMING, FirebaseAuth.getInstance().getCurrentUser().getUid(), calendar);
                // store this trip to firebase
                databaseTrips.child(TripID).setValue(trip);

                Toast.makeText(EditTripActivity.this, R.string.tripUpdatedSuccessfully, Toast.LENGTH_SHORT).show();

                AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);

                Intent intent = new Intent(EditTripActivity.this, AlertReceiver.class);
                intent.putExtra("TripID", trip.getTripID());
                intent.putExtra("TripStartPoint", trip.getTripStartPoint());
                intent.putExtra("TripStartPointLongitude", trip.getTripStartPointLongitude());
                intent.putExtra("TripStartPointLatitude", trip.getTripStartPointLatitude());
                intent.putExtra("TripEndPoint", trip.getTripEndPoint());
                intent.putExtra("TripEndPointLongitude", trip.getTripEndPointLongitude());
                intent.putExtra("TripEndPointLatitude", trip.getTripEndPointLatitude());
                intent.putExtra("TripName", trip.getTripName());
                intent.putExtra("TripDate", trip.getTripDate());
                intent.putExtra("TripTime", trip.getTripTime());
                intent.putExtra("TripStatus", trip.getTripStatus());
                intent.putExtra("UserID", trip.getUserID());
                intent.putExtra("calendar", trip.getTripCalendar());

                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(EditTripActivity.this, TripID.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager1.setExact(AlarmManager.RTC_WAKEUP, calendar, pendingIntent1);
                } else {
                    alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar, pendingIntent1);
                }
            } else {
                if (year1 == 0 && hours1 != 0){
                    Toast.makeText(EditTripActivity.this, R.string.enterValidTripDate, Toast.LENGTH_LONG).show();
                    editTxtDate1.setText("");
                    editTxtTime1.setText("");
                    return;
                } else if (year1 != 0 && hours1 == 0 && min1 == 0) {
                    Toast.makeText(EditTripActivity.this, R.string.enterValidTripTime, Toast.LENGTH_LONG).show();
                    editTxtTime1.setText("");
                    editTxtDate1.setText("");
                    return;
                }

                Trip trip = new Trip(TripID, startPoint, startPointLongitude, startPointLatitude, endPoint, endPointLongitude, endPointLatitude, editTxtTripName.getText().toString(), editTxtDate1.getText().toString(), editTxtTime1.getText().toString(), UPCOMING, FirebaseAuth.getInstance().getCurrentUser().getUid(), calendarAlarm1.getTimeInMillis());
                // store this trip to firebase
                databaseTrips.child(TripID).setValue(trip);

                Toast.makeText(EditTripActivity.this, R.string.tripUpdatedSuccessfully, Toast.LENGTH_SHORT).show();

                AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);

                Intent intent = new Intent(EditTripActivity.this, AlertReceiver.class);
                intent.putExtra("TripID", trip.getTripID());
                intent.putExtra("TripStartPoint", trip.getTripStartPoint());
                intent.putExtra("TripStartPointLongitude", trip.getTripStartPointLongitude());
                intent.putExtra("TripStartPointLatitude", trip.getTripStartPointLatitude());
                intent.putExtra("TripEndPoint", trip.getTripEndPoint());
                intent.putExtra("TripEndPointLongitude", trip.getTripEndPointLongitude());
                intent.putExtra("TripEndPointLatitude", trip.getTripEndPointLatitude());
                intent.putExtra("TripName", trip.getTripName());
                intent.putExtra("TripDate", trip.getTripDate());
                intent.putExtra("TripTime", trip.getTripTime());
                intent.putExtra("TripStatus", trip.getTripStatus());
                intent.putExtra("UserID", trip.getUserID());
                intent.putExtra("calendar", calendarAlarm1.getTimeInMillis());

                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(EditTripActivity.this, TripID.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager1.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm1.getTimeInMillis(), pendingIntent1);
                } else {
                    alarmManager1.set(AlarmManager.RTC_WAKEUP, calendarAlarm1.getTimeInMillis(), pendingIntent1);
                }
            }
            /* AlarmManager */

            startActivity(new Intent(EditTripActivity.this, HomeActivity.class));
            finish();
        }
    }
}
