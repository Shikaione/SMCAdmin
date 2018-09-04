package com.mpetroiu.smc_admin;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {

    private final static String TAG = "AccountFragment";
    private final static int PROFILE_IMAGE_INTENT = 3;
    private final static int COVER_IMAGE_INTENT = 4;

    public StorageReference storageRef;
    public DatabaseReference databaseRef;
    private String user;

    private Uri mProfileUri, mCoverUri;

    private ImageView mCoverImage;
    private CircleImageView mProfileImage;

    private Button mUploadCover, mUploadProfile, mDeleteAccount, mChangePassword, mProfileUpdate;

    private String profileImage, coverImage;

    public AccountFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        storageRef = FirebaseStorage.getInstance().getReference().child("profileImages");
        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mCoverImage = v.findViewById(R.id.imgCover);
        mProfileImage = v.findViewById(R.id.imgUser);

        mUploadCover = v.findViewById(R.id.btnAddProfileCover);
        mUploadProfile = v.findViewById(R.id.btnAddProfilePic);
        mChangePassword = v.findViewById(R.id.btnChangePassword);
        mDeleteAccount = v.findViewById(R.id.btnDeleteAccount);
        mProfileUpdate = v.findViewById(R.id.btnProfileUpdate);


        DatabaseReference userRef = databaseRef.child(user);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profile = dataSnapshot.child("profileImage").getValue().toString();
                String cover = dataSnapshot.child("coverImage").getValue().toString();
                if(!profile.isEmpty() && !cover.isEmpty()){
                    Picasso.get().load(profile).into(mProfileImage);
                    Picasso.get().load(cover).into(mCoverImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (mUploadProfile != null &&
                mUploadCover != null &&
                mProfileUpdate != null) {
            uploadImage();
            updateProfile();
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMAGE_INTENT && resultCode == RESULT_OK) {
            mProfileUri = data.getData();
            Picasso.get().load(mProfileUri).into(mProfileImage);
        }

        if (requestCode == COVER_IMAGE_INTENT && resultCode == RESULT_OK) {
            mCoverUri = data.getData();
            Picasso.get().load(mCoverUri).into(mCoverImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {
        mUploadCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, COVER_IMAGE_INTENT);
            }
        });

        mUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PROFILE_IMAGE_INTENT);
            }
        });
    }

    private void updateProfile() {
        mProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProfileUri != null && mCoverUri != null) {
                    final StorageReference profileRef = storageRef.child("profile_image_" + user +
                            "." + getFileExtension(mProfileUri));
                    final StorageReference coverRef = storageRef.child(("cover_image" + user + "." +
                            getFileExtension(mCoverUri)));

                    profileRef.putFile(mProfileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return profileRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                profileImage = downloadUri.toString();
                                databaseRef.child(user).child("profileImage").setValue(profileImage);
                            }
                        }
                    });

                    coverRef.putFile(mCoverUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return coverRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                coverImage = downloadUri.toString();
                                databaseRef.child(user).child("coverImage").setValue(coverImage);
                                Toast.makeText(getContext(), "Profile Updated",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
