package com.mi.mitrip.historypackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mi.mitrip.AddNotePackage.ModelPackage.Notes;
import com.mi.mitrip.NoteDialogueActivity;
import com.mi.mitrip.R;
import com.mi.mitrip.activity.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.DataViewHolder> {

    ArrayList<Trip> dataModelList = new ArrayList<>();
    Context context;

    DatabaseReference databaseTrips = FirebaseDatabase.getInstance().getReference("trips");
    DatabaseReference databaseNotes = FirebaseDatabase.getInstance().getReference("notes");

    public HistoryListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history_trip, parent, false);
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
        holder.getTxtViewDone().setText(trip.getTripStatus());
        holder.getTxtViewStartPoint().setText(trip.getTripStartPoint());
        holder.getTxtViewEndPoint().setText(trip.getTripEndPoint());

        holder.getImageButtonDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        Toast.makeText(context, R.string.tripDeletedSuccessfully, Toast.LENGTH_SHORT).show();

                        dataModelList.remove(trip);
                        setDataToAdapter(dataModelList);
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

        private TextView txtViewTripDate, txtViewTripTime, txtViewTripName, txtViewTripState, txtViewStartPoint, txtViewEndPoint, txtViewDone;
        private ImageView imgViewNotes, imageViewExpandCard;
        private ImageButton imageButtonDelete;
        private ConstraintLayout constraintLayoutCardInfo;

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

        public ImageButton getImageButtonDelete() {
            if (imageButtonDelete == null) {
                imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);
            }
            return imageButtonDelete;
        }

        public ConstraintLayout getConstraintLayoutCardInfo(){
            if (constraintLayoutCardInfo == null){
                constraintLayoutCardInfo = itemView.findViewById(R.id.constraintLayoutCardInfo);
            }
            return constraintLayoutCardInfo;
        }

        public TextView getTxtViewDone(){
            if (txtViewDone == null){
                txtViewDone = itemView.findViewById(R.id.txtViewDone);
            }
            return txtViewDone;
        }
    }
}
