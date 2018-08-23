package com.mpetroiu.smc_admin;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.*;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.*;

public class PlaceFragment extends Fragment {

    private final static int GALLERY_INTENT = 2;

    public StorageReference storageRef;
    public DatabaseReference databaseRef;

    private Uri mImageUri;
    private EditText mImageName, mOwner, mEmail, mPhone, mLocation, mLocationType, mAddress;

    public PlaceFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);


        mOwner = view.findViewById(R.id.etOwner);
        mEmail = view.findViewById(R.id.etEmail);
        mPhone = view.findViewById(R.id.etPhone);
        mLocation = view.findViewById(R.id.etLocationName);
        mLocationType = view.findViewById(R.id.etLocationType);
        mAddress = view.findViewById(R.id.etAddress);

        Button mUploadPicture = (Button) view.findViewById(R.id.uploadPhoto);
        mUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        Button mUploadePlace = view.findViewById(R.id.updatePlace);
        mUploadePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String owner = mOwner.getText().toString();
                String email = mEmail.getText().toString();
                String phone = mPhone.getText().toString();
                String location = mLocation.getText().toString();
                String type = mLocationType.getText().toString();
                String address = mAddress.getText().toString();

                Map<String, String> placeMap = new HashMap<>();

                placeMap.put("owner", owner);
                placeMap.put("email", email);
                placeMap.put("phone", phone);
                placeMap.put("location", location);
                placeMap.put("type", type);
                placeMap.put("address", address);

                databaseRef.child("Place"+ System.currentTimeMillis()).setValue(placeMap);
                uploadFile();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storageRef = FirebaseStorage.getInstance().getReference().child("placeImages");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Places").child("Place");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mImageUri = data.getData();

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
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
                        Upload upload = new Upload(mLocation.getText().toString().trim(),
                                downloadUri.toString());
                        databaseRef.child("placeImages").push().setValue(upload);
                        Toast.makeText(getContext(), "Upload Done.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
