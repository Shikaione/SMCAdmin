package com.mpetroiu.smc_admin;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class NotificationsFragment extends Fragment {

    private Context mContext;

    private Spinner mLocationSpinner;

    private EditText mTitle, mDescription;

    private String currentUser;

    private DatabaseReference mDatabaseRef;

    private Button mSendButton;

    public NotificationsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        mContext = getContext();

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mLocationSpinner = v.findViewById(R.id.locationSpinner);

        mTitle = v.findViewById(R.id.etTitle);
        mDescription = v.findViewById(R.id.etNotificationMessage);

        mSendButton = v.findViewById(R.id.sendNotification);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Places");

        populateSpinner();

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        return v;
    }

    private void populateSpinner() {
        Query byUser = mDatabaseRef.orderByChild("user").equalTo(currentUser);

        byUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                final List<String> places = new ArrayList<String>();

                for (DataSnapshot ds : children) {
                    Upload upload = ds.getValue(Upload.class);
                    if (upload != null) {
                        String placeName = upload.getLocation();
                        places.add(placeName);
                    }
                }

                ArrayAdapter<String> placeAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, places);
                placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mLocationSpinner.setAdapter(placeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    String tag = mLocationSpinner.getSelectedItem().toString().replace(" ", "");
                    String title = mTitle.getText().toString();
                    String message = mDescription.getText().toString();

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NDlkYWI3OTUtNGVjYS00ZmZmLWFmNzktYmE5NzRmN2VhMWU0");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"3d77d6ce-38ce-4daf-9403-93430eb611a8\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"subscribed_topic\", \"relation\": \"=\", \"value\": \"" + tag + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"headings\": {\"en\": \"" + title + "\"},"
                                + "\"contents\": {\"en\": \"" + message + "\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }
}




