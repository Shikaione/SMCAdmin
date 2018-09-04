package com.mpetroiu.smc_admin;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class PlaceFragment extends Fragment implements PlaceAdapter.OnItemClickListener {

    private FloatingActionButton addNewPlace;
    private RecyclerView mRecyclerView;
    private PlaceAdapter mAdapter;
    private List<Upload> mUploads;
    private DatabaseReference mDbReference;
    private FirebaseStorage mStorage;

    private FirebaseAuth mAuth;
    private String currentUser;
    private ProgressBar mProgressCircle;

    public PlaceFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        mDbReference = FirebaseDatabase.getInstance().getReference().child("Places");
        mStorage = FirebaseStorage.getInstance();
        mProgressCircle = view.findViewById(R.id.circularProgressBar);
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
        mAdapter = new PlaceAdapter(mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        showPlace();
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

        byUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                mUploads.clear();
                for(DataSnapshot ds : children){
                    Upload upload = ds.getValue(Upload.class);
                    upload.setKey(ds.getKey());
                    mUploads.add(upload);
                }
                mProgressCircle.setVisibility(View.INVISIBLE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onWhatEverClick(int position) {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        UpdatePlaceFragment updatePlace = new UpdatePlaceFragment();
        Bundle args = new Bundle();
        args.putString("key", mUploads.get(position).getKey());
        updatePlace.setArguments(args);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                updatePlace).addToBackStack(null).commit();

        Log.e(TAG, "Key is :" + mUploads.get(position).getKey());

    }

    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getThumbnail());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDbReference.child(selectedKey).removeValue();
                Toast.makeText(getContext(), "Place deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
