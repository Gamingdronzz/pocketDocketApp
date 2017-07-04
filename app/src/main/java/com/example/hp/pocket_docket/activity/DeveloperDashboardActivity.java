package com.example.hp.pocket_docket.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.fragments.HomeFragment;
import com.example.hp.pocket_docket.fragments.MyInfoFragment;
import com.example.hp.pocket_docket.fragments.MyProjectsFragment;
import com.example.hp.pocket_docket.shared_preferences.SavedSharedPreference;

public class DeveloperDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tv1;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tv1 = (TextView) header.findViewById(R.id.devName);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tv1.setText(SavedSharedPreference.getName(DeveloperDashboardActivity.this));
        fab.setVisibility(View.INVISIBLE);
        Fragment f=new HomeFragment();
        FragmentTransaction ftc=getSupportFragmentManager().beginTransaction();
        ftc.replace(R.id.content_developer_dashboard,f);
        ftc.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.myInfo) {
            getSupportFragmentManager().popBackStack();
            Fragment f = new MyInfoFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_developer_dashboard, f);
            ft.commit();
            ft.addToBackStack(null);
        }
        if (id == R.id.myProjects) {
            getSupportFragmentManager().popBackStack();
            Fragment f = new MyProjectsFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_developer_dashboard, f);
            ft.commit();
            ft.addToBackStack(null);
        }
        if (id == R.id.signout) {
            if (SavedSharedPreference.getFlag(this)) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeveloperDashboardActivity.this);
                alertDialogBuilder
                        .setMessage("You are active on a module! Clock out first.")
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("Logout Anyway",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent1 = new Intent(DeveloperDashboardActivity.this, LoginActivity.class);
                                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        SavedSharedPreference.clearPref(DeveloperDashboardActivity.this);
                                        startActivity(intent1);
                                        finish();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                Intent intent1 = new Intent(DeveloperDashboardActivity.this, LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SavedSharedPreference.clearPref(DeveloperDashboardActivity.this);
                startActivity(intent1);
                finish();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

