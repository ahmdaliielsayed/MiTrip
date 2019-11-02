package com.example.mitrip.upcomingpackage;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitrip.AddNotePackage.ModelPackage.Notes;
import com.example.mitrip.AddNotePackage.ViewPackage.AddNoteActivity;
import com.example.mitrip.FloatingWidgetShowService;
import com.example.mitrip.NoteDialogueActivity;
import com.example.mitrip.R;
import com.example.mitrip.activity.AlertReceiver;
import com.example.mitrip.activity.EditTripActivity;
import com.example.mitrip.activity.Trip;
import com.example.mitrip.activity.TripActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UpcomingListAdapter extends RecyclerView.Adapter<UpcomingListAdapter.DataViewHolder> {

    ArrayList<Trip> dataModelList = new ArrayList<>();
    Activity context;

    DatabaseReference databaseTrips = FirebaseDatabase.getInstance().getReference("trips");
    DatabaseReference databaseNotes = FirebaseDatabase.getInstance().getReference("notes");

    public static final int SYSTEM_ALERT_WINDOW_PERMISSION = 7;

    public UpcomingListAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upcoming_trip, parent, false);
        DataViewHolder holder = new DataViewHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(final DataViewHolder holder, final int position) {

        final Trip trip = dataModelList.get(position);

        holder.getTxtViewTripDate().setText(trip.getTripDate());
        holder.getTxtViewTripTime().setText(trip.getTripTime());
        holder.getTxtViewTripName().setText(trip.getTripName());
        holder.getTxtViewTripState().setText(trip.getTripStatus());
        holder.getTxtViewStartPoint().setText(trip.getTripStartPoint());
        holder.getTxtViewEndPoint().setText(trip.getTripEndPoint());

        holder.getImageButtonPopUp().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.imageButtonPopUp);
                popupMenu.getMenuInflater().inflate(R.menu.menu_card_upcoming, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_add_notes:
                                Intent openNoteActivity = new Intent(context, AddNoteActivity.class);
                                openNoteActivity.putExtra("tripID", trip.getTripID());
                                context.startActivity(openNoteActivity);
                                break;
                            case R.id.item_edit:
                                Intent intent = new Intent(context, EditTripActivity.class);
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
                                intent.putExtra("TripCalendar", trip.getTripCalendar());
                                context.startActivity(intent);
                                break;
                            case R.id.item_Delete:

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(R.string.areYouSureToDeleteThisTrip);
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        databaseTrips.child(trip.getTripID()).removeValue();
                                        databaseNotes.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                                                    Notes note = noteSnapshot.getValue(Notes.class);

                                                    if (note.getTripID().equals(trip.getTripID())){
                                                        databaseNotes.child(note.getNoteID()).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                                        Intent iDelete = new Intent(context, AlertReceiver.class);
                                        iDelete.putExtra("TripID", trip.getTripID());
                                        iDelete.putExtra("TripStartPoint", trip.getTripStartPoint());
                                        iDelete.putExtra("TripStartPointLongitude", trip.getTripStartPointLongitude());
                                        iDelete.putExtra("TripStartPointLatitude", trip.getTripStartPointLatitude());
                                        iDelete.putExtra("TripEndPoint", trip.getTripEndPoint());
                                        iDelete.putExtra("TripEndPointLongitude", trip.getTripEndPointLongitude());
                                        iDelete.putExtra("TripEndPointLatitude", trip.getTripEndPointLatitude());
                                        iDelete.putExtra("TripName", trip.getTripName());
                                        iDelete.putExtra("TripDate", trip.getTripDate());
                                        iDelete.putExtra("TripTime", trip.getTripTime());
                                        iDelete.putExtra("TripStatus", trip.getTripStatus());
                                        iDelete.putExtra("UserID", trip.getUserID());
                                        iDelete.putExtra("TripCalendar", trip.getTripCalendar());

                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, trip.getTripID().hashCode(), iDelete, PendingIntent.FLAG_UPDATE_CURRENT);
                                        alarmManager.cancel(pendingIntent);

                                        Toast.makeText(context, R.string.tripDeletedSuccessfully, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                if (Build.VERSION.SDK_INT >= 26) {
                                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
                                }
                                else {
                                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                }
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();

                                break;
                            case R.id.item_cancel:

                                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                                Intent i = new Intent(context, AlertReceiver.class);
                                i.putExtra("TripID", trip.getTripID());
                                i.putExtra("TripStartPoint", trip.getTripStartPoint());
                                i.putExtra("TripStartPointLongitude", trip.getTripStartPointLongitude());
                                i.putExtra("TripStartPointLatitude", trip.getTripStartPointLatitude());
                                i.putExtra("TripEndPoint", trip.getTripEndPoint());
                                i.putExtra("TripEndPointLongitude", trip.getTripEndPointLongitude());
                                i.putExtra("TripEndPointLatitude", trip.getTripEndPointLatitude());
                                i.putExtra("TripName", trip.getTripName());
                                i.putExtra("TripDate", trip.getTripDate());
                                i.putExtra("TripTime", trip.getTripTime());
                                i.putExtra("TripStatus", trip.getTripStatus());
                                i.putExtra("UserID", trip.getUserID());
                                i.putExtra("TripCalendar", trip.getTripCalendar());

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, trip.getTripID().hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.cancel(pendingIntent);

                                Trip tripCancelled = new Trip(trip.getTripID(), trip.getTripStartPoint(), trip.getTripStartPointLongitude(), trip.getTripStartPointLatitude(), trip.getTripEndPoint(), trip.getTripEndPointLongitude(), trip.getTripEndPointLatitude(), trip.getTripName(), trip.getTripDate(), trip.getTripTime(), TripActivity.CANCELLED, FirebaseAuth.getInstance().getCurrentUser().getUid(), trip.getTripCalendar());
                                // store this trip to firebase
                                databaseTrips.child(trip.getTripID()).setValue(tripCancelled);

                                Toast.makeText(context, R.string.tripCancelledSuccessfully, Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        holder.getImageViewExpandCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getConstraintLayoutCardInfo().getVisibility() == View.VISIBLE){
                    holder.getConstraintLayoutCardInfo().setVisibility(View.GONE);
                } else {
                    holder.getConstraintLayoutCardInfo().setVisibility(View.VISIBLE);

                    Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.open_card);
                    animation.setDuration(500);
                    holder.getConstraintLayoutCardInfo().setAnimation(animation);
                    holder.getConstraintLayoutCardInfo().animate();
                    animation.start();
                }
            }
        });

        holder.getBtnStartTrip().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                    new androidx.appcompat.app.AlertDialog.Builder(context)
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
                    Intent intent = new Intent (context, FloatingWidgetShowService.class);
                    intent.putExtra("tripID", trip.getTripID());
                    intent.putExtra("tripName", trip.getTripName());
                    context.startService(intent);
                    context.finish();
                } else if (Settings.canDrawOverlays(context)) {
                    Intent intent = new Intent (context, FloatingWidgetShowService.class);
                    intent.putExtra("tripID", trip.getTripID());
                    intent.putExtra("tripName", trip.getTripName());
                    context.startService(intent);
                    context.finish();
                } else {
                    new androidx.appcompat.app.AlertDialog.Builder(context)
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

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent iDone = new Intent(context, AlertReceiver.class);
                iDone.putExtra("TripID", trip.getTripID());
                iDone.putExtra("TripStartPoint", trip.getTripStartPoint());
                iDone.putExtra("TripStartPointLongitude", trip.getTripStartPointLongitude());
                iDone.putExtra("TripStartPointLatitude", trip.getTripStartPointLatitude());
                iDone.putExtra("TripEndPoint", trip.getTripEndPoint());
                iDone.putExtra("TripEndPointLongitude", trip.getTripEndPointLongitude());
                iDone.putExtra("TripEndPointLatitude", trip.getTripEndPointLatitude());
                iDone.putExtra("TripName", trip.getTripName());
                iDone.putExtra("TripDate", trip.getTripDate());
                iDone.putExtra("TripTime", trip.getTripTime());
                iDone.putExtra("TripStatus", trip.getTripStatus());
                iDone.putExtra("UserID", trip.getUserID());
                iDone.putExtra("TripCalendar", trip.getTripCalendar());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, trip.getTripID().hashCode(), iDone, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);

//                String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + trip.getTripStartPointLongitude() + "," + trip.getTripStartPointLatitude() + "&daddr=" + trip.getTripEndPointLongitude() + "," + trip.getTripEndPointLatitude();
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                context.startActivity(Intent.createChooser(intent, context.getText(R.string.selectApplication)));

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + trip.getTripEndPoint());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(Intent.createChooser(mapIntent, context.getText(R.string.selectApplication)));

                Trip tripDone = new Trip(trip.getTripID(), trip.getTripStartPoint(), trip.getTripStartPointLongitude(), trip.getTripStartPointLatitude(), trip.getTripEndPoint(), trip.getTripEndPointLongitude(), trip.getTripEndPointLatitude(), trip.getTripName(), trip.getTripDate(), trip.getTripTime(), TripActivity.DONE, FirebaseAuth.getInstance().getCurrentUser().getUid(), trip.getTripCalendar());
                // store this trip to firebase
                databaseTrips.child(trip.getTripID()).setValue(tripDone);
                context.finishAffinity();
            }
        });

        holder.getImgViewNotes().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openNoteDialogue = new Intent(context, NoteDialogueActivity.class);
                openNoteDialogue.putExtra("tripID", trip.getTripID());
                openNoteDialogue.putExtra("tripName", trip.getTripName());
                context.startActivity(openNoteDialogue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModelList.size() > 0 ? dataModelList.size() : 0;
    }

    public void setDataToAdapter(ArrayList<Trip> dataModelList) {
        this.dataModelList = dataModelList;
        notifyDataSetChanged();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView txtViewTripDate, txtViewTripTime, txtViewTripName, txtViewTripState, txtViewStartPoint, txtViewEndPoint;
        private ImageView imgViewNotes, imageViewExpandCard;
        private ImageButton imageButtonPopUp;
        private ConstraintLayout constraintLayoutCardInfo;
        private Button btnStartTrip;

        public DataViewHolder(View itemView) {
            super(itemView);
        }

        public TextView getTxtViewTripDate() {
            if (txtViewTripDate == null) {
                txtViewTripDate = itemView.findViewById(R.id.txtViewTripDate);
            }
            return txtViewTripDate;
        }

        public TextView getTxtViewTripTime() {
            if (txtViewTripTime == null) {
                txtViewTripTime = itemView.findViewById(R.id.txtViewTripTime);
            }
            return txtViewTripTime;
        }

        public TextView getTxtViewTripName() {
            if (txtViewTripName == null) {
                txtViewTripName = itemView.findViewById(R.id.txtViewTripName);
            }
            return txtViewTripName;
        }

        public TextView getTxtViewTripState() {
            if (txtViewTripState == null) {
                txtViewTripState = itemView.findViewById(R.id.txtViewTripState);
            }
            return txtViewTripState;
        }

        public TextView getTxtViewStartPoint() {
            if (txtViewStartPoint == null) {
                txtViewStartPoint = itemView.findViewById(R.id.txtViewStartPoint);
            }
            return txtViewStartPoint;
        }

        public TextView getTxtViewEndPoint() {
            if (txtViewEndPoint == null) {
                txtViewEndPoint = itemView.findViewById(R.id.txtViewEndPoint);
            }
            return txtViewEndPoint;
        }

        public ImageView getImgViewNotes() {
            if (imgViewNotes == null) {
                imgViewNotes = itemView.findViewById(R.id.imgViewNotes);
            }
            return imgViewNotes;
        }

        public ImageView getImageViewExpandCard() {
            if (imageViewExpandCard == null) {
                imageViewExpandCard = itemView.findViewById(R.id.imageViewExpandCard);
            }
            return imageViewExpandCard;
        }

        public ImageButton getImageButtonPopUp() {
            if (imageButtonPopUp == null) {
                imageButtonPopUp = itemView.findViewById(R.id.imageButtonPopUp);
            }
            return imageButtonPopUp;
        }

        public ConstraintLayout getConstraintLayoutCardInfo(){
            if (constraintLayoutCardInfo == null){
                constraintLayoutCardInfo = itemView.findViewById(R.id.constraintLayoutCardInfo);
            }
            return constraintLayoutCardInfo;
        }

        public Button getBtnStartTrip(){
            if (btnStartTrip == null){
                btnStartTrip = itemView.findViewById(R.id.btnStartTrip);
            }
            return btnStartTrip;
        }
    }

    public void RuntimePermissionForUser() {

        Intent PermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));

        context.startActivityForResult(PermissionIntent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }
}
