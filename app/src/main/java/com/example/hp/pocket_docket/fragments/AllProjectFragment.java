package com.example.hp.pocket_docket.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.adapter.AllProjectAdapter;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.beans.Project;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllProjectFragment extends Fragment {

    private TextView tv;
    private ListView lv;
    private HTTPRequestProcessor req;
    private APIConfiguration api;
    private String baseURL, url1, res;
    private Project project;
    private ArrayList<Project> alist;
    private AllProjectAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_project, container, false);

        tv = (TextView) view.findViewById(R.id.loading);
        lv = (ListView) view.findViewById(R.id.allList);
        getActivity().findViewById(R.id.ProjectList).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        req = new HTTPRequestProcessor();
        api = new APIConfiguration();
        baseURL = api.getApi();
        url1 = baseURL + "ProjectAPI/GetProjectListing";

        new ShowAllListTask().execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.ProjectList).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.ProjectList).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }

    private class ShowAllListTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            tv.setText("Loading Projects...");
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
                alist = new ArrayList<>();
                if (success) {
                    JSONArray jsonArray = jsonObject.getJSONArray("responseData");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        project = new Project();
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        String id = object.getString("ProjectId");
                        String title = object.getString("Title");
                        String type = object.getString("ProjectType");
                        project.setEnd(object.getString("EndDate"));
                        project.setId(id);
                        project.setTitle(title);
                        project.setType(type);
                        alist.add(project);
                    }
                    adapter = new AllProjectAdapter(getContext(), alist);
                    lv.setAdapter(adapter);
                } else
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                tv.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(),"Some Error Occured",Toast.LENGTH_LONG).show();
            }
        }


    }
}
