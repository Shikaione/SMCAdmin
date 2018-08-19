package com.mpetroiu.smc_admin;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View header = findViewById(R.id.nav_view);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(null);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            setupNavigationDrawerContent(mNavigationView);
        }

        setupNavigationDrawerContent(mNavigationView);

        View headerView = mNavigationView.getHeaderView(0);
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

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.account:
                                menuItem.setChecked(true);
                                mActionBar.setTitle(menuItem.getTitle());
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.place:
                                menuItem.setChecked(true);
                                mActionBar.setTitle(menuItem.getTitle());
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.notification:
                                menuItem.setChecked(true);
                                mActionBar.setTitle(menuItem.getTitle());
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.logout:
                                menuItem.setChecked(true);
                                Toast.makeText(MainActivity.this, "See you again ! ", Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(new Intent(MainActivity.this, LoginOptions.class));
                                // mAuth.signOut();
                                //  mGoogleSignInClient.signOut();
                                //  LoginManager.getInstance().logOut();
                                finish();
                                return true;
                        }
                        return true;
                    }
                });
    }
}
