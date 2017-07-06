package com.example.hp.pocket_docket.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.activity.AdminDashboardActivity;
import com.example.hp.pocket_docket.adapter.ModuleAdapter;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.beans.Module;
import com.example.hp.pocket_docket.beans.Project;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 16-05-2017.
 */

public class ProjectDetailFragment extends Fragment {
    private TextView name, loading;
    private ListView lv1;
    private String ID, id, pname, status;
    private String baseURL, url, res;
    private HTTPRequestProcessor httpRequestProcessor;
    private APIConfiguration apiConfiguration;
    private ArrayList<Module> moduleList;
    private Module m;
    private Project p;
    private ModuleAdapter adapter;
    private Bundle bundle;
    private AlertDialog statusDialog;
    private FloatingActionButton fab;
    private FragmentTransaction ft;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_detail, container, false);
        name = (TextView) view.findViewById(R.id.detailName);
        lv1 = (ListView) view.findViewById(R.id.moduleList);
        loading = (TextView) view.findViewById(R.id.loading);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        bundle = this.getArguments();
        p = bundle.getParcelable("Project");
        pname = p.getTitle();
        ID = p.getId();
        name.setText(pname);
        lv1.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.ProjectList).setVisibility(View.GONE);

        registerForContextMenu(lv1);

        httpRequestProcessor = new HTTPRequestProcessor();
        apiConfiguration = new APIConfiguration();
        baseURL = apiConfiguration.getApi();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Fragment f = new AddModuleFragment();
                    f.setArguments(bundle);
                    ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_admin_dashboard, f);
                    ft.commit();
                    ft.addToBackStack(null);
                } catch (Exception e) {

                }
            }
        });
        if (Network.isNetworkAvailable(getContext())) {
            new LoadModuleListTask().execute(ID);
        } else
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_LONG).show();

        return view;
    }

    public void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fab.setOnClickListener(null);
        final AdminDashboardActivity mainActivity = (AdminDashboardActivity) getActivity();
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new AddProjectFragment();
                ft = mainActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_admin_dashboard, f);
                ft.commit();
                ft.addToBackStack(null);
            }
        });
        getActivity().findViewById(R.id.ProjectList).setVisibility(View.VISIBLE);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.module_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info1 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index1 = info1.position;
        m = moduleList.get(index1);
        switch (item.getItemId()) {
            case R.id.edit1:
                Bundle bundle = new Bundle();
                bundle.putParcelable("Project", p);
                bundle.putParcelable("Module", m);
                Fragment f1 = new EditModuleFragment();
                f1.setArguments(bundle);
                FragmentTransaction ft1 = getActivity().getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.content_admin_dashboard, f1);
                ft1.commit();
                ft1.addToBackStack(null);
                return true;
            case R.id.status:
                if (!Validator.checkStarted(m.getMstart()))
                    Toast.makeText(getContext(), "Module Not Started", Toast.LENGTH_LONG).show();
                else {
                    final CharSequence[] items = {"0-10 %", "10-50 %", "50-90 % ", "90-100 % "};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                            new UpdateStatusTask().execute(m.getMno(), status);
                            statusDialog.dismiss();
                        }
                    });
                    statusDialog = builder.create();
                    statusDialog.show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private class LoadModuleListTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setText("Loading...");
        }

        @Override
        protected String doInBackground(String... params) {
            id = params[0];
            url = baseURL + "SprintAPI/GetProjectSprintListing/" + id;
            try {
                res = httpRequestProcessor.gETRequestProcessor(url);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }
            return res;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.setText("");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(s);
                moduleList = new ArrayList<Module>();
                JSONArray responseData = jsonObject.getJSONArray("responseData");
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    if (responseData.length() == 0)
                        name.setText("No Modules in Project " + pname + "!");
                    else {
                        for (int i = 0; i < responseData.length(); i++) {
                            JSONObject object = responseData.getJSONObject(i);
                            String stitle = object.getString("Title");
                            String sid = object.getString("SprintId");
                            String desc = object.getString("Description");
                            String status = object.getString("Status");
                            String start = object.getString("StartDate");
                            String end = object.getString("EndDate");
                            m = new Module();
                            m.setMno(sid);
                            m.setMtitle(stitle);
                            m.setMdesc(desc);
                            m.setStatus(status);
                            m.setMstart(start);
                            m.setMend(end);
                            m.setTitle(p.getTitle());
                            m.setId(p.getId());
                            moduleList.add(m);
                        }
                        adapter = new ModuleAdapter(getContext(), moduleList);
                        lv1.setAdapter(adapter);
                    }
                } else
                    Toast.makeText(getContext(), "Error loading records!", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateStatusTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            url = baseURL + "SprintAPI/UpdateSprintStatus/" + params[0] + "/" + params[1];
            try {
                res = httpRequestProcessor.pOSTRequestProcessor("", url);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getContext(), "Status Updated", Toast.LENGTH_LONG).show();
                    new LoadModuleListTask().execute(ID);
                } else
                    Toast.makeText(getContext(), "Error Updating Status", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
