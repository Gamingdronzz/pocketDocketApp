package com.example.hp.pocket_docket.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.adapter.MemberAdapter;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.beans.Member;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewMembersActivity extends AppCompatActivity {

    private ListView lv;
    private HTTPRequestProcessor req;
    private APIConfiguration api;
    private String baseURL, url, message, res;
    boolean success;
    String fname, lname;
    Member member;
    ArrayList<Member> al = new ArrayList<Member>();

    MemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);
        lv = (ListView) findViewById(R.id.lvViewMembers);

        req = new HTTPRequestProcessor();
        api = new APIConfiguration();
        baseURL = api.getApi();
        url = baseURL + "MemberAPI/GetApplicationMemberList";
        if (Network.isNetworkAvailable(ViewMembersActivity.this))
            new ViewTask().execute();
        else
            Toast.makeText(ViewMembersActivity.this, "No Internet", Toast.LENGTH_LONG).show();
    }

    public class ViewTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            res = req.gETRequestProcessor(url);
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                success = jsonObject.getBoolean("success");
                message = jsonObject.getString("message");
                if (success) {
                    JSONArray responseData = jsonObject.getJSONArray("responseData");
                    for (int i = 0; i < responseData.length(); i++) {
                        member = new Member();
                        JSONObject object = responseData.getJSONObject(i);
                        String type = object.getString("UserTypeId");
                        String eid = object.getString("MemberCode");
                        if (type.equals("4")) {
                            if (eid.equals(null)) {
                                continue;
                            } else {
                                fname = object.getString("FName");
                                lname = object.getString("LName");
                                member.setName(fname, lname);
                                member.setEmpId(eid);
                                member.setDesig(object.getString("Designation"));
                                member.setContact(object.getString("MobileNo"));
                                member.setEmail(object.getString("EmailId"));
                                member.setGender(object.getString("Gender"));
                                al.add(member);
                            }
                        }
                    }
                    adapter = new MemberAdapter(ViewMembersActivity.this, al);
                    lv.setAdapter(adapter);
                } else {
                    Toast.makeText(ViewMembersActivity.this, "Error loading records!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
