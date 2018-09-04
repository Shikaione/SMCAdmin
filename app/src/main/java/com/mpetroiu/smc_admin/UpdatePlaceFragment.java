package com.mpetroiu.smc_admin;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class UpdatePlaceFragment extends Fragment {

    private final static String TAG = "UpdatePlaceFragment";
    private final static int GALLERY_INTENT = 2;

    public StorageReference storageRef;
    public DatabaseReference databaseRef;

    private Uri mImageUri;
    private EditText mOwner, mEmail, mPhone, mLocation, mLocationType, mAddress;
    private Button mUploadPicture, mUploadPlace;
    private String user, thumbnailUrl, thumb, key;
    private ImageView previewThumb;

    public UpdatePlaceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_place, container, false);

        storageRef = FirebaseStorage.getInstance().getReference().child("placeImages");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Places");
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mOwner = v.findViewById(R.id.etOwner);
        mEmail = v.findViewById(R.id.etEmail);
        mPhone = v.findViewById(R.id.etPhone);
        mLocation = v.findViewById(R.id.etLocationName);
        mLocationType = v.findViewById(R.id.etLocationType);
        mAddress = v.findViewById(R.id.etAddress);
        previewThumb = v.findViewById(R.id.previewThumbnail);

        mUploadPicture = v.findViewById(R.id.uploadPhoto);
        mUploadPlace = v.findViewById(R.id.updatePlace);

        if (getArguments() != null) {
            key = getArguments().getString("key");
            Log.d(TAG, "this is key : " + key);
        }

        retrieveData();

        if (mUploadPlace != null) {
            uploadPicture();
            uploadPlace();
        }


        return v;
    }

    public void retrieveData() {
        DatabaseReference retrieve = databaseRef.child(key);

        retrieve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Place place = dataSnapshot.getValue(Place.class);
                final String owner, email, address, location, type, phone;
                if (place != null) {
                    owner = place.getOwner();
                    email = place.getEmail();
                    address = place.getEmail();
                    location = place.getLocation();
                    type = place.getType();
                    phone = place.getPhone();
                    thumb = place.getThumbnail();

                    mOwner.setText(owner);
                    mEmail.setText(email);
                    mAddress.setText(address);
                    mLocation.setText(location);
                    mLocationType.setText(type);
                    mPhone.setText(phone);
                    Picasso.get().load(thumb).into(previewThumb);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(previewThumb);
        }
    }

    private void uploadPicture() {
        mUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPlace() {
        mUploadPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadInfo();
            }
        });
    }

    private void uploadInfo() {
        if (mImageUri != null) {
            final StorageReference imageRef = storageRef.child("place" + mImageUri.getLastPathSegment() +
                    "." + getFileExtension(mImageUri));

            imageRef.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        thumbnailUrl = downloadUri.toString();

                        String owner = mOwner.getText().toString();
                        String email = mEmail.getText().toString();
                        String phone = mPhone.getText().toString();
                        String location = mLocation.getText().toString();
                        String type = mLocationType.getText().toString();
                        String address = mAddress.getText().toString();

                        Map<String, String> placeMap = new HashMap<>();

                        placeMap.put("user", user);
                        placeMap.put("owner", owner);
                        placeMap.put("email", email);
                        placeMap.put("phone", phone);
                        placeMap.put("location", location);
                        placeMap.put("type", type);
                        placeMap.put("address", address);
                        placeMap.put("thumbnail", thumbnailUrl);

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(key, placeMap);

                        databaseRef.updateChildren(childUpdates);

                        Toast.makeText(getContext(), "Place updated.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

}
