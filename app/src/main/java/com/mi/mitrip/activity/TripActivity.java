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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class TripActivity extends AppCompatActivity {

    private static final String TAG = "TripActivity";

    public static final String UPCOMING = "Upcoming";
    public static final String CANCELLED = "Cancelled";
    public static final String DONE = "Done";

    private PlacesClient placesClient;
    private String APIKey, startPoint, endPoint;
    private double startPointLongitude, startPointLatitude, endPointLongitude, endPointLatitude;
    private EditText editTxtTripName, editTxtDate1, editTxtTime1, editTxtDate2, editTxtTime2;
    private DatePickerDialog.OnDateSetListener calender1;
    private DatePickerDialog.OnDateSetListener calender2;
    private TimePickerDialog timePickerDialog;

    Date date, myDateCheck;
    private int mHour, mMin, hours1, min1, year1, month1, dayOfMonth1, hours2, min2, year2, month2, dayOfMonth2;

    private Spinner spinner;
    private LinearLayout linearLayout;

    private Button btnAdd;

    DatabaseReference databaseTrips1;
    DatabaseReference databaseTrips2;

    Calendar calendarAlarm1, calendarAlarm2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        editTxtTripName = findViewById(R.id.editTxtTripName);
        editTxtDate1 = findViewById(R.id.editTxtDate1);
        editTxtTime1 = findViewById(R.id.editTxtTime1);
        editTxtDate2 = findViewById(R.id.editTxtDate2);
        editTxtTime2 = findViewById(R.id.editTxtTime2);

        spinner = findViewById(R.id.spinner);
        linearLayout = findViewById(R.id.linearLayout);

        btnAdd = findViewById(R.id.btnAdd);

        databaseTrips1 = FirebaseDatabase.getInstance().getReference("trips");
        databaseTrips2 = FirebaseDatabase.getInstance().getReference("trips");

        //APIKey = "AIzaSyCShYBlgJ1uU6HDzGC8XBOcipdwMIhlcA8";
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
                Toast.makeText(TripActivity.this, place.getName(), Toast.LENGTH_LONG);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Toast.makeText(TripActivity.this, R.string.error, Toast.LENGTH_LONG);
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
                Toast.makeText(TripActivity.this, place.getName(), Toast.LENGTH_LONG);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Toast.makeText(TripActivity.this, R.string.error, Toast.LENGTH_LONG);
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
                        TripActivity.this,
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
//                calendarAlarm1.set(year, month, dayOfMonth);
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
                    Toast.makeText(TripActivity.this, R.string.enterValidTripDate, Toast.LENGTH_LONG).show();
                } else {
                    editTxtDate1.setText(startDate);
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

                timePickerDialog = new TimePickerDialog(TripActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                            Toast.makeText(TripActivity.this, R.string.enterValidTripDate, Toast.LENGTH_LONG).show();
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

        /*** checked spinner for trip type ***/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        String selectedItemText1 = (String) adapterView.getItemAtPosition(i);
                        Toast.makeText(TripActivity.this, selectedItemText1, Toast.LENGTH_LONG).show();
                        linearLayout.setVisibility(View.GONE);
                        break;
                    case 1:
                        String selectedItemText2 = (String) adapterView.getItemAtPosition(i);
                        Toast.makeText(TripActivity.this, selectedItemText2, Toast.LENGTH_LONG).show();
                        linearLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*** trip2 date ***/
        editTxtDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        TripActivity.this,
                        calender2,
                        year,
                        month,
                        day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
        calender2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;

                year2 = year;
                month2 = month;
                dayOfMonth2 = dayOfMonth;

                String time = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                String startDate = dayOfMonth + "/" + month + "/" + year;

                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar myCalOne = Calendar.getInstance();
                Calendar myCalTwo = Calendar.getInstance();
                try {
                    date = format.parse(time);
                    myDateCheck = format.parse(startDate);

                    Date dateOne = format.parse(editTxtDate1.getText().toString());
                    myCalOne.setTime(dateOne);
                    Date dateTwo = format.parse(startDate);
                    myCalTwo.setTime(dateTwo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (myDateCheck.equals(null)) {
                    try {
                        myDateCheck = format.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (editTxtDate1.getText().toString().isEmpty()) {
                    Toast.makeText(TripActivity.this, R.string.youMustInsertTripDateFirst, Toast.LENGTH_LONG).show();
                } else if (myCalTwo.before(myCalOne)) {
                    Toast.makeText(TripActivity.this, R.string.youMustInsertTripReturnDateAfterStartDate, Toast.LENGTH_LONG).show();
                } else if (myDateCheck.before(date)) {
                    Toast.makeText(TripActivity.this, R.string.enterValidTripReturnDate, Toast.LENGTH_LONG).show();
                } else {
                    editTxtDate2.setText(startDate);
                }
            }
        };

        /*** trip2 time ***/
        editTxtTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(TripActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                        hours2 = hourOfDay;
                        min2 = minutes;
                        calendarAlarm2 = Calendar.getInstance();
                        calendarAlarm2.set(Calendar.YEAR, year2);
                        calendarAlarm2.set(Calendar.MONTH, month2 - 1);
                        calendarAlarm2.set(Calendar.DAY_OF_MONTH, dayOfMonth2);
                        calendarAlarm2.set(Calendar.HOUR_OF_DAY, hours2);
                        calendarAlarm2.set(Calendar.MINUTE, min2);
                        calendarAlarm2.set(Calendar.SECOND, 0);

                        if (editTxtDate1.getText().toString().isEmpty()) {
                            Toast.makeText(TripActivity.this, R.string.youMustInsertTripDateFirst, Toast.LENGTH_LONG).show();
                        } else if (editTxtTime1.getText().toString().isEmpty()) {
                            Toast.makeText(TripActivity.this, R.string.youMustInsertTripTimeFirst, Toast.LENGTH_LONG).show();
                        } else if (editTxtDate2.getText().toString().isEmpty()) {
                            Toast.makeText(TripActivity.this, R.string.YouMustInsertTripReturnDateFirst, Toast.LENGTH_LONG).show();
                        } else if (editTxtDate1.getText().toString().equals(editTxtDate2.getText().toString())) {
                            if (hourOfDay < hours1 || (hourOfDay == hours1 && minutes <= min1)) {
                                Toast.makeText(TripActivity.this, R.string.YouMustInsertTripReturnTimeAfterStartTime, Toast.LENGTH_LONG).show();
                            } else if (hourOfDay < 10 && minutes >= 10) {
                                editTxtTime2.setText("0" + hourOfDay + ":" + minutes);
                            } else if (hourOfDay < 10 && minutes < 10) {
                                editTxtTime2.setText("0" + hourOfDay + ":0" + minutes);
                            } else if (hourOfDay >= 10 && minutes < 10) {
                                editTxtTime2.setText(hourOfDay + ":0" + minutes);
                            } else if (hourOfDay >= 10 && minutes >= 10) {
                                editTxtTime2.setText(hourOfDay + ":" + minutes);
                            }
                        } else if (hourOfDay < 10 && minutes >= 10) {
                            editTxtTime2.setText("0" + hourOfDay + ":" + minutes);
                        } else if (hourOfDay < 10 && minutes < 10) {
                            editTxtTime2.setText("0" + hourOfDay + ":0" + minutes);
                        } else if (hourOfDay >= 10 && minutes < 10) {
                            editTxtTime2.setText(hourOfDay + ":0" + minutes);
                        } else if (hourOfDay >= 10 && minutes >= 10) {
                            editTxtTime2.setText(hourOfDay + ":" + minutes);
                        }
                    }
                }, 0, 0, false);
                timePickerDialog.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addTrip();
            }
        });
    }

    private void addTrip() {
        if (startPoint == null || startPoint == "") {
            new AlertDialog.Builder(TripActivity.this)
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
            new AlertDialog.Builder(TripActivity.this)
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
            Toast.makeText(TripActivity.this, R.string.enterValidTripName, Toast.LENGTH_LONG).show();
            editTxtTripName.requestFocus();
        } else if (editTxtDate1.getText().toString().isEmpty()) {
            Toast.makeText(TripActivity.this, R.string.enterValidTripDate, Toast.LENGTH_LONG).show();
        } else if (editTxtTime1.getText().toString().isEmpty()) {
            Toast.makeText(TripActivity.this, R.string.enterValidTripTime, Toast.LENGTH_LONG).show();
        } else if (spinner.getSelectedItem().toString().equals("Round Trip") || spinner.getSelectedItem().toString().equals("ذهاباََ و إياباََ")) {
            if (editTxtDate2.getText().toString().isEmpty()) {
                Toast.makeText(TripActivity.this, R.string.enterValidTripReturnDate, Toast.LENGTH_LONG).show();
            } else if (editTxtTime2.getText().toString().isEmpty()) {
                Toast.makeText(TripActivity.this, R.string.enterValidTripReturnTime, Toast.LENGTH_LONG).show();
            } else {
                // unique string for first trip
                String id1 = databaseTrips1.push().getKey();
                Trip trip1 = new Trip(id1, startPoint, startPointLongitude, startPointLatitude, endPoint, endPointLongitude, endPointLatitude, editTxtTripName.getText().toString(), editTxtDate1.getText().toString(), editTxtTime1.getText().toString(), UPCOMING, FirebaseAuth.getInstance().getCurrentUser().getUid(), calendarAlarm1.getTimeInMillis());
                // store this trip to firebase
                databaseTrips1.child(id1).setValue(trip1);

                /* AlarmManager */
                AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);

                Intent intent1 = new Intent(TripActivity.this, AlertReceiver.class);
                intent1.putExtra("TripID", trip1.getTripID());
                intent1.putExtra("TripStartPoint", trip1.getTripStartPoint());
                intent1.putExtra("TripStartPointLongitude", trip1.getTripStartPointLongitude());
                intent1.putExtra("TripStartPointLatitude", trip1.getTripStartPointLatitude());
                intent1.putExtra("TripEndPoint", trip1.getTripEndPoint());
                intent1.putExtra("TripEndPointLongitude", trip1.getTripEndPointLongitude());
                intent1.putExtra("TripEndPointLatitude", trip1.getTripEndPointLatitude());
                intent1.putExtra("TripName", trip1.getTripName());
                intent1.putExtra("TripDate", trip1.getTripDate());
                intent1.putExtra("TripTime", trip1.getTripTime());
                intent1.putExtra("TripStatus", trip1.getTripStatus());
                intent1.putExtra("UserID", trip1.getUserID());
                intent1.putExtra("calendar", calendarAlarm1.getTimeInMillis());

                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(TripActivity.this, id1.hashCode(), intent1, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager1.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm1.getTimeInMillis(), pendingIntent1);
                } else {
                    alarmManager1.set(AlarmManager.RTC_WAKEUP, calendarAlarm1.getTimeInMillis(), pendingIntent1);
                }
                /* AlarmManager */

                // unique string for return trip
                String id2 = databaseTrips2.push().getKey();
                Trip trip2 = new Trip(id2, endPoint, endPointLongitude, endPointLatitude, startPoint, startPointLongitude, startPointLatitude, editTxtTripName.getText().toString(), editTxtDate2.getText().toString(), editTxtTime2.getText().toString(), UPCOMING, FirebaseAuth.getInstance().getCurrentUser().getUid(), calendarAlarm2.getTimeInMillis());
                // store this trip to firebase
                databaseTrips2.child(id2).setValue(trip2);

                /* AlarmManager */
                AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);

                Intent intent2 = new Intent(TripActivity.this, AlertReceiver.class);
                intent2.putExtra("TripID", trip2.getTripID());
                intent2.putExtra("TripStartPoint", trip2.getTripStartPoint());
                intent2.putExtra("TripStartPointLongitude", trip2.getTripStartPointLongitude());
                intent2.putExtra("TripStartPointLatitude", trip2.getTripStartPointLatitude());
                intent2.putExtra("TripEndPoint", trip2.getTripEndPoint());
                intent2.putExtra("TripEndPointLongitude", trip2.getTripEndPointLongitude());
                intent2.putExtra("TripEndPointLatitude", trip2.getTripEndPointLatitude());
                intent2.putExtra("TripName", trip2.getTripName());
                intent2.putExtra("TripDate", trip2.getTripDate());
                intent2.putExtra("TripTime", trip2.getTripTime());
                intent2.putExtra("TripStatus", trip2.getTripStatus());
                intent2.putExtra("UserID", trip2.getUserID());
                intent2.putExtra("calendar", calendarAlarm2.getTimeInMillis());

                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(TripActivity.this, id2.hashCode(), intent2, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager2.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm2.getTimeInMillis(), pendingIntent2);
                } else {
                    alarmManager2.set(AlarmManager.RTC_WAKEUP, calendarAlarm2.getTimeInMillis(), pendingIntent2);
                }
                /* AlarmManager */

                Toast.makeText(TripActivity.this, R.string.tripAddedSuccessfully, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TripActivity.this, HomeActivity.class));
                finish();
            }
        } else {
            // unique string for each trip
            String id = databaseTrips1.push().getKey();
            Trip trip = new Trip(id, startPoint, startPointLongitude, startPointLatitude, endPoint, endPointLongitude, endPointLatitude,
                    editTxtTripName.getText().toString(), editTxtDate1.getText().toString(), editTxtTime1.getText().toString(),
                    UPCOMING, FirebaseAuth.getInstance().getCurrentUser().getUid(), calendarAlarm1.getTimeInMillis());
            // store this trip to firebase
            databaseTrips1.child(id).setValue(trip);

            Toast.makeText(TripActivity.this, R.string.tripAddedSuccessfully, Toast.LENGTH_SHORT).show();

            /* AlarmManager */
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent intent = new Intent(TripActivity.this, AlertReceiver.class);
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

            PendingIntent pendingIntent = PendingIntent.getBroadcast(TripActivity.this, id.hashCode(), intent, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm1.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarm1.getTimeInMillis(), pendingIntent);
            }
            /* AlarmManager */

            finish();
        }
    }
}
