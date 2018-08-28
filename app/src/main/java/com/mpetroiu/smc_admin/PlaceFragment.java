package com.mpetroiu.smc_admin;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlaceFragment extends Fragment {

    private FloatingActionButton addNewPlace;
    private RecyclerView mRecyclerView;
    private PlaceAdapter mAdapter;
    private List<Upload> mUploads;
    private DatabaseReference mDbReference;
    private FirebaseAuth mAuth;
    private String currentUser;

    public PlaceFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        mDbReference = FirebaseDatabase.getInstance().getReference().child("Places");

        addNewPlace = view.findViewById(R.id.addPlace);

        addPlace();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.placeRecycle);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUploads = new ArrayList<>();
        showPlace();
        mAdapter = new PlaceAdapter(mUploads);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void addPlace(){
        addNewPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                NewPlaceFragment exploreFragment = new NewPlaceFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                        exploreFragment).addToBackStack(null).commit();
            }
        });
    }

    public void showPlace(){
        Query byUser = mDbReference.orderByChild("user").equalTo(currentUser);

        byUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot ds : children){
                    Upload upload = ds.getValue(Upload.class);
                    mUploads.add(upload);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
