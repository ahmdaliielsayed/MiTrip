package com.example.mitrip.AddNotePackage.ViewPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitrip.AddNotePackage.ModelPackage.Notes;
import com.example.mitrip.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.DataViewHolder> {

    ArrayList<Notes> dataModelList = new ArrayList<>();
    Context context;

    DatabaseReference databaseNotes = FirebaseDatabase.getInstance().getReference("notes");

    public NotesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public NotesAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_note, parent, false);
        DataViewHolder holder = new DataViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final DataViewHolder holder, final int position) {

        final Notes notes = dataModelList.get(position);

        holder.getTvNote().setText(notes.getNote());

        holder.getIvDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.areYouSureToDeleteThisNote);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseNotes.child(notes.getNoteID()).removeValue();
                        Toast.makeText(context, R.string.noteDeletedSuccessfully, Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public int getItemCount() {
        return dataModelList.size() > 0 ? dataModelList.size() : 0;
    }

    public void setDataToAdapter(ArrayList<Notes> dataModelList) {
        this.dataModelList = dataModelList;
        notifyDataSetChanged();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNote;
        private ImageView ivDelete;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public TextView getTvNote() {
            if (tvNote == null) {
                tvNote = itemView.findViewById(R.id.tvNote);
            }
            return tvNote;
        }

        public ImageView getIvDelete() {
            if (ivDelete == null) {
                ivDelete = itemView.findViewById(R.id.ivDelete);
            }
            return ivDelete;
        }
    }
}
