package com.mpetroiu.smc_admin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDataRef;

    private DrawerLayout mDrawerLayout;
    private ActionBar mActionBar;

    private AccountFragment mAccountFragment;
    private PlaceFragment mPlaceFragment;
    private NotificationsFragment mNotificationsFragment;

    private TextView mUserName, mUserEmail;

    private ImageView mCover;
    private CircleImageView mProfile;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mDataRef = FirebaseDatabase.getInstance().getReference().child("users");

        View header = findViewById(R.id.nav_header);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(null);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            setupNavigationDrawerContent(mNavigationView);
        }

        setupNavigationDrawerContent(mNavigationView);

        View headerView = mNavigationView.getHeaderView(0);

        mUserName = (TextView) headerView.findViewById(R.id.logName);
        mUserEmail = (TextView) headerView.findViewById(R.id.userEmail);
        mProfile = headerView.findViewById(R.id.profile);
        mCover = headerView.findViewById(R.id.cover);

        FrameLayout mFrame = findViewById(R.id.main_frame);

        mAccountFragment = new AccountFragment();
        mPlaceFragment = new PlaceFragment();
        mNotificationsFragment = new NotificationsFragment();

        setFragment(mPlaceFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String uid = mAuth.getCurrentUser().getUid();

        DatabaseReference userRef = mDataRef.child(uid);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserName.setText(dataSnapshot.child("name").getValue().toString());
                mUserEmail.setText(dataSnapshot.child("email").getValue().toString());
                if (dataSnapshot.child("profileImage").exists()) {
                    String profile = dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.get().load(profile).into(mProfile);
                }
                if (dataSnapshot.child("coverImage").exists()) {
                    String cover = dataSnapshot.child("coverImage").getValue().toString();
                    Picasso.get().load(cover).into(mCover);
                }else{

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        switch (id) {
                            case R.id.account:
                                setFragment(mAccountFragment);
                                navigationView.setCheckedItem(id);
                                mActionBar.setTitle(menuItem.getTitle());
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.place:
                                setFragment(mPlaceFragment);
                                navigationView.setCheckedItem(id);
                                mActionBar.setTitle(menuItem.getTitle());
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.notification:
                                setFragment(mNotificationsFragment);
                                navigationView.setCheckedItem(id);
                                mActionBar.setTitle(menuItem.getTitle());
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.logout:
                                navigationView.setCheckedItem(id);
                                Toast.makeText(MainActivity.this, "See you again ! ", Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(new Intent(MainActivity.this, LoginOptions.class));
                                mAuth.signOut();
                                mGoogleSignInClient.signOut();
                                finish();
                                return true;
                        }
                        return true;
                    }
                });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
