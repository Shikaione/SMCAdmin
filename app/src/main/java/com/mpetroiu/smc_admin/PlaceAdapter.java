package com.mpetroiu.smc_admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {

    private List<Upload> mUploads;
    private Upload uploadCurrent;

    private Context mContext;

    private DatabaseReference mDataRef;

    public PlaceAdapter(List<Upload> uploads){
        mUploads = uploads;
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card, parent, false);
        PlaceHolder pH = new PlaceHolder(view);

        mContext = parent.getContext();

        mDataRef = FirebaseDatabase.getInstance().getReference();

        return pH;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolder holder, int position) {
        uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getLocation());
        Picasso.get()
                .load(uploadCurrent.getThumbnail())
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class PlaceHolder extends RecyclerView.ViewHolder{

        public TextView textViewName;
        public ImageView imageView;

        public PlaceHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.placeName);
            imageView = itemView.findViewById(R.id.thumbnailPlace);
        }
    }
}
