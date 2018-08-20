package com.mpetroiu.smc_admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;

    private EditText mEmail, mVerifyEmail, mPassword, mVerifyPassword, mName, mCompany, mType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mName = (EditText) findViewById(R.id.);
        mEmail = (EditText) findViewById(R.id.);
        mVerifyEmail = (EditText) findViewById(R.id.);
        mCompany = (EditText) findViewById(R.id.);
        mType = (EditText) findViewById(R.id.);
        mPassword = (EditText) findViewById(R.id.);
        mVerifyPassword = (EditText) findViewById(R.id.);

        Button mCreate = (Button) findViewById(R.id.);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(mEmail.getText().toString(), mPassword.getText().toString());
            }
        });
    }

    private void createAccount(String email, String password){
        Log.d(TAG, "signUp:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            startActivity(new Intent(RegisterActivity.this, LoginOptions.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = mName.getText().toString();
        if(TextUtils.isEmpty(name)){
            mName.setError("Required");
            valid = false;
        }else{
            mName.setError(null);
        }

        String email = mEmail.getText().toString();
        String verifyEmail = mVerifyEmail.getText().toString();
        if (TextUtils.isEmpty(email) && !email.equals(verifyEmail)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        String verifyPass = mVerifyPassword.getText().toString();
        if (TextUtils.isEmpty(password) && !password.equals(verifyPass)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        String company = mCompany.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mCompany.setError("Required.");
            valid = false;
        } else {
            mCompany.setError(null);
        }

        String type = mType.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mType.setError("Required.");
            valid = false;
        } else {
            mType.setError(null);
        }

        return valid;
    }
}
