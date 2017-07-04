package com.example.hp.pocket_docket.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.shared_preferences.SavedSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 28-06-2017.
 */

public class HomeFragment extends Fragment {
    private TextView active, total, complete;
    private HTTPRequestProcessor req;
    private APIConfiguration api;
    private String baseURL, url, res;
    private boolean success;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        active = (TextView) view.findViewById(R.id.activeProject);
        total = (TextView) view.findViewById(R.id.totalProjects);
        complete = (TextView) view.findViewById(R.id.completedProjects);
        req = new HTTPRequestProcessor();
        api = new APIConfiguration();
        baseURL = api.getApi();
        url = baseURL + "SprintMemberAssociationAPI/GetMySprintList/" + SavedSharedPreference.getCode(getContext());

        new GetInfoTask().execute();
        return view;

    }

    private class GetInfoTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            total.setText("Please wait...");
        }

        @Override
        protected String doInBackground(String... params) {
            res = req.gETRequestProcessor(url);
            return res;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int count = 0;
            try {
                JSONObject jsonObject = new JSONObject(s);
                success = jsonObject.getBoolean("success");
                if (success) {
                    JSONArray responseData = jsonObject.getJSONArray("responseData");
                    if (responseData.length() != 0) {
                        for (int i = 0; i < responseData.length(); i++) {
                            JSONObject object = responseData.getJSONObject(i);
                            String end = object.getString("EndDate");
                            if (Validator.checkEnded(end)) {
                                count++;
                            }
                        }
                    }
                    total.setText("Total Projects:   " + responseData.length());
                    complete.setText("Projects Complete:   " + count);
                }
                if (SavedSharedPreference.getFlag(getContext())) {
                    active.setText("ACTIVE\nModule: " + SavedSharedPreference.getCurModule(getContext()) + "\nProject: " + SavedSharedPreference.getCurPoject(getContext()));
                } else {
                    active.setText("NOT ACTIVE!\nView your Projects and select a Project to Work");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
