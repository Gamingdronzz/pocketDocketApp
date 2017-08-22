package com.example.hp.pocket_docket.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.shared_preferences.SavedSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 28-06-2017.
 */

public class MyInfoFragment extends Fragment {
    TextView name, id, desig, contact, email, username;
    private HTTPRequestProcessor req;
    private APIConfiguration api;
    private String baseURL, url, res;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_info, container, false);

        name = (TextView) view.findViewById(R.id.myName);
        username = (TextView) view.findViewById(R.id.myUserName);
        id = (TextView) view.findViewById(R.id.myId);
        desig = (TextView) view.findViewById(R.id.myDesig);
        contact = (TextView) view.findViewById(R.id.myPhone);
        email = (TextView) view.findViewById(R.id.myEmail);

        api = new APIConfiguration();
        req = new HTTPRequestProcessor();
        baseURL = api.getApi();

        new GetMyDetailsTask().execute();
        return view;
    }

    private class GetMyDetailsTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            url = baseURL + "MemberAPI/GetMemberDetail/" + SavedSharedPreference.getCode(getContext());
            res = req.gETRequestProcessor(url);
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    JSONArray jsonArray = jsonObject.getJSONArray("responseData");
                    JSONObject object = (JSONObject) jsonArray.get(0);
                    name.setText(object.getString("FName") + " " + object.getString("LName"));
                    username.setText(SavedSharedPreference.getUserName(getContext()));
                    id.setText(object.getString("MemberCode"));
                    desig.setText(object.getString("Designation"));
                    contact.setText(object.getString("MobileNo"));
                    email.setText(object.getString("EmailId"));
                } else
                    Toast.makeText(getContext(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Some Error Occured", Toast.LENGTH_LONG).show();
            }
        }
    }

}
