package com.example.hp.pocket_docket.activity;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.adapter.ProjectAdapter;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.beans.Project;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;
import com.example.hp.pocket_docket.fragments.AddProjectFragment;
import com.example.hp.pocket_docket.fragments.AllProjectFragment;
import com.example.hp.pocket_docket.fragments.EditProjectFragment;
import com.example.hp.pocket_docket.fragments.ProjectDetailFragment;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;
import com.example.hp.pocket_docket.shared_preferences.SavedSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public FloatingActionButton fab;
    private TextView tv, loading;
    private ListView lv;
    private HTTPRequestProcessor req;
    private APIConfiguration api;
    private String baseURL, url1, res;
    private Project project;
    private ArrayList<Project> al;
    private ProjectAdapter adapter;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tv = (TextView) header.findViewById(R.id.txtAdminName);
        tv.setText(SavedSharedPreference.getName(AdminDashboardActivity.this));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        loading = (TextView) findViewById(R.id.loading);
        lv = (ListView) findViewById(R.id.ProjectList);
        registerForContextMenu(lv);

        req = new HTTPRequestProcessor();
        api = new APIConfiguration();
        baseURL = api.getApi();
        url1 = baseURL + "ProjectAPI/GetProjectListing";

        if (Network.isNetworkAvailable(AdminDashboardActivity.this)) {
            new ShowProjectListTask().execute();
        } else
            Toast.makeText(this, "No Internet", Toast.LENGTH_LONG).show();


        //----------------ListView click--------------------------
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Project p = (Project) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Project", p);
                Fragment f = new ProjectDetailFragment();
                f.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ftc = fm.beginTransaction();
                ftc.replace(R.id.content_admin_dashboard, f);
                ftc.commit();
                ftc.addToBackStack(null);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new AddProjectFragment();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_admin_dashboard, f);
                ft.commit();
                ft.addToBackStack(null);
            }
        });
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                new ShowProjectListTask().execute();
            }
        });

    }

    //----------on back button press------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    //----------------context menu---------------------------
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            Project p = al.get(index);
            Bundle bundle = new Bundle();
            bundle.putParcelable("Project", p);
            switch (item.getItemId()) {
                case R.id.edit:
                    Fragment f1 = new EditProjectFragment();
                    f1.setArguments(bundle);
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content_admin_dashboard, f1);
                    ft1.commit();
                    ft1.addToBackStack(null);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        } catch (Exception e) {
            return false;
        }
    }


    //-----------------Navigation drawer item selection---------------
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.registerMember) {
            Intent intent1 = new Intent(AdminDashboardActivity.this, RegisterMembersActivity.class);
            startActivity(intent1);
        } else if (id == R.id.viewMembers) {
            Intent intent3 = new Intent(AdminDashboardActivity.this, ViewMembersActivity.class);
            startActivity(intent3);
        } else if (id == R.id.newProject) {
            Fragment f = new AddProjectFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_admin_dashboard, f);
            ft.commit();
            ft.addToBackStack(null);
        } else if (id == R.id.complete) {
            Fragment f = new AllProjectFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_admin_dashboard, f);
            ft.commit();
            ft.addToBackStack(null);
        } else if (id == R.id.logout) {
            Intent intent4 = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SavedSharedPreference.clearPref(AdminDashboardActivity.this);
            startActivity(intent4);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //--------------------------------------------Show projects--------------------------------------------------------------------
    public class ShowProjectListTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setText("Loading Projects...");
        }

        @Override
        protected String doInBackground(String... params) {
            res = req.gETRequestProcessor(url1);
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                boolean success = jsonObject.getBoolean("success");
                al = new ArrayList<>();
                if (success) {
                    JSONArray jsonArray = jsonObject.getJSONArray("responseData");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        String endDate = object.getString("EndDate");
                        if (!Validator.checkEnded(endDate)) {
                            String id = object.getString("ProjectId");
                            String title = object.getString("Title");
                            String desc = object.getString("Description");
                            String startDate = object.getString("StartDate");
                            String type = object.getString("ProjectType");
                            project = new Project(id, title, desc, startDate, endDate, type);
                            al.add(project);
                        }
                    }
                    adapter = new ProjectAdapter(AdminDashboardActivity.this, al);
                    lv.setAdapter(adapter);
                } else
                    Toast.makeText(AdminDashboardActivity.this, "Error", Toast.LENGTH_SHORT).show();
                loading.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(AdminDashboardActivity.this, "Some Error Occured", Toast.LENGTH_LONG).show();
            }
        }
    }
}