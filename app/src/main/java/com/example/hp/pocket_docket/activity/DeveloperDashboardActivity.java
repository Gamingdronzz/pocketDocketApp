package com.example.hp.pocket_docket.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.fragments.HomeFragment;
import com.example.hp.pocket_docket.fragments.MyInfoFragment;
import com.example.hp.pocket_docket.fragments.MyProjectsFragment;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;
import com.example.hp.pocket_docket.shared_preferences.SavedSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DeveloperDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tv1;
    private FloatingActionButton fab;
    private long totalMins, diff, diffHr, diffMin, diffsec;
    private int hour, minute, second, hr, min, sec, year, month, day, yr, mon, d, h, min1;
    private boolean success, workFlag;
    private String baseURL, url, url1, url2, jsonStringToPost, jsonResponseString, res;
    private String message, code, currentModule, currentModuleId, currentProject, inTime, total, status;
    private HTTPRequestProcessor req;
    private APIConfiguration api;
    private Calendar c;
    private AlertDialog statusDialog;

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
        fab.setVisibility(View.INVISIBLE);
        tv1.setText(SavedSharedPreference.getName(DeveloperDashboardActivity.this));

        code = SavedSharedPreference.getCode(this);
        workFlag = SavedSharedPreference.getFlag(this);
        if (workFlag) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.INVISIBLE);
        }
        req = new HTTPRequestProcessor();
        api = new APIConfiguration();
        baseURL = api.getApi();

        Fragment f = new HomeFragment();
        FragmentTransaction ftc = getSupportFragmentManager().beginTransaction();
        ftc.replace(R.id.content_developer_dashboard, f);
        ftc.commit();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    c = Calendar.getInstance();
                    yr = c.get(Calendar.YEAR);
                    mon = c.get(Calendar.MONTH);
                    d = c.get(Calendar.DAY_OF_MONTH);
                    hr = c.get(Calendar.HOUR_OF_DAY);
                    min = c.get(Calendar.MINUTE);
                    sec = c.get(Calendar.SECOND);

                    LayoutInflater li = LayoutInflater.from(DeveloperDashboardActivity.this);
                    View promptsView1 = li.inflate(R.layout.out_time, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeveloperDashboardActivity.this);
                    alertDialogBuilder.setView(promptsView1);

                    final TextView totalTime = (TextView) promptsView1.findViewById(R.id.totalTime);
                    final TextView timeOut = (TextView) promptsView1.findViewById(R.id.timeOut);
                    final TextView in = (TextView) promptsView1.findViewById(R.id.in);
                    final TextView out = (TextView) promptsView1.findViewById(R.id.out);

                    timeOut.setText(hr + ":" + min + ":" + sec);
                    in.setText("In: " + SavedSharedPreference.getInTime(DeveloperDashboardActivity.this));
                    out.setText("Out: " + d + "/" + mon + "/" + yr + " " + hr + ":" + min + ":" + sec);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    try {
                        diff = ((format.parse(d + "/" + mon + "/" + yr + " " + hr + ":" + min + ":" + sec).getTime() - format.parse(SavedSharedPreference.getInTime(DeveloperDashboardActivity.this)).getTime()));
                        diffHr = (diff / (1000 * 60 * 60));
                        diffMin = (diff / (1000 * 60)) % 60;
                        diffsec = (diff / (1000)) % 60;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    totalTime.setText(String.valueOf(diffHr) + ":" + String.valueOf(diffMin) + ":" + String.valueOf(diffsec));
                    alertDialogBuilder
                            .setCancelable(true)
                            .setPositiveButton("Update",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (Network.isNetworkAvailable(DeveloperDashboardActivity.this)) {
                                                totalMins = diffHr * 60 + diffMin;
                                                total = String.valueOf(totalMins);
                                                new UpdateTimeTask().execute(total, SavedSharedPreference.getCurModuleId(DeveloperDashboardActivity.this), code);
                                            } else
                                                Toast.makeText(DeveloperDashboardActivity.this, "Could Not Update Time.Check your Network Coonection", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });


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

    private class UpdateTimeTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("SprintId", params[1]);
                jsonObject.put("MemberId", params[2]);
                jsonObject.put("TimeSpend", params[0]);
                baseURL = api.getApi();
                url2 = baseURL + "SprintMemberTimeAssociationAPI/AddNewAssociation";
                jsonStringToPost = jsonObject.toString();
                jsonResponseString = req.pOSTRequestProcessor(jsonStringToPost, url2);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(DeveloperDashboardActivity.this, "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }
            return jsonResponseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Response String", s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                success = jsonObject.getBoolean("success");
                message = jsonObject.getString("message");
                if (success) {
                    workFlag = false;
                    SavedSharedPreference.setFlag(DeveloperDashboardActivity.this, workFlag);
                    fab.setVisibility(View.INVISIBLE);
                    Toast.makeText(DeveloperDashboardActivity.this, "Time updated", Toast.LENGTH_LONG).show();
                    final CharSequence[] items = {"0-10 %", "10-50 %", "50-90 % ", "90-100 % "};
                    AlertDialog.Builder builder = new AlertDialog.Builder(DeveloperDashboardActivity.this);
                    builder.setTitle("Select The Module Progress");
                    builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    status = "1";
                                    break;
                                case 1:
                                    status = "2";
                                    break;
                                case 2:
                                    status = "3";
                                    break;
                                case 3:
                                    status = "4";
                                    break;
                            }
                            new UpdateProgressTask().execute(status);
                            statusDialog.dismiss();
                        }
                    });
                    statusDialog = builder.create();
                    statusDialog.show();
                    refreshFragment();
                } else {
                    Toast.makeText(DeveloperDashboardActivity.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_developer_dashboard);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(currentFragment);
        ft.attach(currentFragment);
        ft.commit();
    }

    private class UpdateProgressTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            url = baseURL + "SprintMemberAssociationAPI/UpdateSprintStatus/" + SavedSharedPreference.getAscId(DeveloperDashboardActivity.this) + "/" + params[0];
            try {
                res = req.gETRequestProcessor(url);
            } catch (Exception e) {
                Toast.makeText(DeveloperDashboardActivity.this, "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    Toast.makeText(DeveloperDashboardActivity.this, "Progress Updated", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(DeveloperDashboardActivity.this, "Error Updating Progress", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

