package com.example.hp.pocket_docket.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.beans.Member;
import com.example.hp.pocket_docket.beans.Module;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 03-07-2017.
 */

public class AssignMembersFragment extends Fragment {
    private HTTPRequestProcessor httpRequestProcessor;
    private APIConfiguration apiConfiguration;
    private String baseURL, url, url1, url2;
    private String jsonPostString, jsonResponseString, res;
    private Button btn;
    private ListView lv;
    private TextView pro, mod, current;
    private String sid, sstart, send, sdesc;
    private String[] mlist;
    boolean success;
    private Module m;
    private Member member;
    private ArrayList<Member> memberListing, currentMemberList;
    private ArrayAdapter<Member> adapter;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assign_members, container, false);

        pro = (TextView) view.findViewById(R.id.assignProject);
        mod = (TextView) view.findViewById(R.id.assignModule);
        current = (TextView) view.findViewById(R.id.currentMembers);
        lv = (ListView) view.findViewById(R.id.selectMembers);
        btn = (Button) view.findViewById(R.id.assignSubmit);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);


        Bundle bundle = this.getArguments();
        m = bundle.getParcelable("Module");

        pro.setText(pro.getText() + m.getTitle());
        mod.setText(mod.getText() + m.getMtitle());
        fab.setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.ProjectList).setVisibility(View.GONE);

        httpRequestProcessor = new HTTPRequestProcessor();
        apiConfiguration = new APIConfiguration();
        baseURL = apiConfiguration.getApi();
        url = baseURL + "SprintMemberAssociationAPI/AddSprintAssociation";
        url1 = baseURL + "MemberAPI/GetApplicationMemberList";

        if (Network.isNetworkAvailable(getContext())) {
            new GetCurrentMembersTask().execute(m.getMno());
        } else
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_LONG).show();

        //Button Click
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Network.isNetworkAvailable(getContext())) {
                    sid = m.getMno();
                    sdesc = m.getMdesc();
                    sstart = m.getMstart();
                    send = m.getMend();
                    Member member;
                    StringBuffer sb = new StringBuffer();

                    SparseBooleanArray checked = lv.getCheckedItemPositions();
                    for (int i = 0; i < checked.size(); i++) {
                        int position = checked.keyAt(i);
                        if (checked.valueAt(i)) {
                            member = adapter.getItem(position);
                            sb.append(member.getId()).append(",");
                        }
                    }
                    new AssignTask().execute(sid, sdesc, sstart, send, sb.toString());
                } else
                    Toast.makeText(getContext(), "Unable to Assign! No Internet", Toast.LENGTH_LONG).show();

            }

        });
        return view;
    }

    public void onResume() {
        super.onResume();
        fab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.ProjectList).setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
    }

    private class GetCurrentMembersTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            url2 = baseURL + "SprintAPI/GetSprintListing/" + params[0];
            try {
                res = httpRequestProcessor.gETRequestProcessor(url2);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            currentMemberList = new ArrayList<>();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(s);
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    JSONArray responseData = jsonObject.getJSONArray("responseData");
                    JSONObject mem = responseData.getJSONObject(0);
                    JSONArray memberlist = mem.getJSONArray("MemberList");
                    if (memberlist.length() == 0) {
                        current.setText("No Members in Module");
                    } else {
                        for (int j = 0; j < memberlist.length(); j++) {
                            member = new Member();
                            JSONObject object = memberlist.getJSONObject(j);
                            String mId = object.getString("MemberId");
                            String name = object.getString("MemberName");
                            member.setId(mId);
                            member.setName(name, " ");
                            currentMemberList.add(member);
                            if (j == 0)
                                current.setText(name);
                            else
                                current.setText(current.getText() + "\n" + name);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new LoadMemberTask().execute();                //Load Members in ListView
        }
    }

    // ------------------------------------------Load ListView task ------------------------------------------------
    private class LoadMemberTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                res = httpRequestProcessor.gETRequestProcessor(url1);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            boolean flag;
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                success = jsonObject.getBoolean("success");
                if (success) {
                    String fname, lname, id;
                    Member mem;
                    memberListing = new ArrayList<Member>();
                    JSONArray responseData = jsonObject.getJSONArray("responseData");
                    for (int i = 0; i < responseData.length(); i++) {
                        JSONObject object = responseData.getJSONObject(i);
                        flag = false;
                        String type = object.getString("UserTypeId");
                        if (type.equals("4")) {
                            id = object.getString("MemberId");
                            for (Member m : currentMemberList) {
                                if (m.getId().equals(id)) {
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                mem = new Member();
                                fname = object.getString("FName");
                                lname = object.getString("LName");
                                String eId = object.getString("MemberCode");
                                mem.setName(fname, lname);
                                mem.setId(id);
                                mem.setEmpId(eId);
                                memberListing.add(mem);
                            }
                        }
                    }
                    adapter = new ArrayAdapter<Member>(getContext(), android.R.layout.simple_list_item_multiple_choice, memberListing);
                    lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    lv.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private class AssignTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            sid = params[0];
            String list = params[4];
            mlist = list.split(",");
            JSONObject obj;
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("SprintId", sid);
                jsonObject.put("Description", params[1]);
                jsonObject.put("StartDate", params[2]);
                jsonObject.put("EndDate", params[3]);
                for (String m : mlist) {
                    obj = new JSONObject();
                    obj.put("SprintMemberId", m);
                    jsonArray.put(obj);
                }
                jsonObject.put("SprintMemberList", jsonArray);
                jsonPostString = jsonObject.toString();
                jsonResponseString = httpRequestProcessor.pOSTRequestProcessor(jsonPostString, url);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }
            return jsonResponseString;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                success = jsonObject.getBoolean("success");
                if (success) {
                    Toast.makeText(getContext(), "Members Assigned", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Select Members", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}