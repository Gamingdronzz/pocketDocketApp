package com.example.hp.pocket_docket.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.adapter.ModuleMemberAdapter;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.beans.Member;
import com.example.hp.pocket_docket.beans.Module;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 14-06-2017.
 */

public class ModuleDetailFragment extends Fragment {
    private ListView lv3;
    private TextView tv3, loading;
    private String url, baseURL, res;
    private HTTPRequestProcessor httpRequestProcessor;
    private APIConfiguration apiConfiguration;
    private Member m;
    private ModuleMemberAdapter adapter;
    private ArrayList<Member> allMemberList;
    private ArrayList<Member> sprintMemberList;
    private ArrayList<Member> infoAddedList;
    private Bundle bundle;
    private FloatingActionButton fab;
    Module module;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_module_detail, container, false);

        bundle = this.getArguments();
        module = bundle.getParcelable("Module");

        lv3 = (ListView) view.findViewById(R.id.memberList);
        tv3 = (TextView) view.findViewById(R.id.moduleName);
        loading = (TextView) view.findViewById(R.id.loading);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        tv3.setText(module.getMtitle() + " : " + module.getTitle());
        fab.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.ProjectList).setVisibility(View.GONE);

        httpRequestProcessor = new HTTPRequestProcessor();
        apiConfiguration = new APIConfiguration();
        baseURL = apiConfiguration.getApi();
        new GetMemberListTask().execute();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("Module", module);
                Fragment f = new AssignMembersFragment();
                f.setArguments(bundle1);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_admin_dashboard, f);
                ft.commit();
                ft.addToBackStack(null);
            }
        });
        return view;
    }

    public void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.ProjectList).setVisibility(View.VISIBLE);
    }

    private class GetMemberListTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            loading.setText("Loading...");
        }

        @Override
        protected String doInBackground(String... params) {
            url = baseURL + "MemberAPI/GetApplicationMemberList";
            try {
                res = httpRequestProcessor.gETRequestProcessor(url);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }
            return res;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(s);
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    JSONArray responseData = jsonObject.getJSONArray("responseData");
                    allMemberList = new ArrayList<>();
                    for (int i = 0; i < responseData.length(); i++) {
                        m = new Member();
                        JSONObject object = responseData.getJSONObject(i);
                        String contact = object.getString("MobileNo");
                        String desig = object.getString("Designation");
                        String code = object.getString("MemberId");
                        String empId = object.getString("MemberCode");
                        m.setId(code);
                        m.setContact(contact);
                        m.setDesig(desig);
                        m.setEmpId(empId);
                        allMemberList.add(m);
                    }
                    updateList(allMemberList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateList(ArrayList<Member> ml) {
        allMemberList = ml;
        new MemberInfoTask().execute(module.getMno());
    }

    private class MemberInfoTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            url = baseURL + "SprintAPI/GetSprintListing/" + params[0];
            try {
                res = httpRequestProcessor.gETRequestProcessor(url);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(s);
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    JSONArray responseData = jsonObject.getJSONArray("responseData");
                    JSONObject mem = responseData.getJSONObject(0);
                    JSONArray memberlist = mem.getJSONArray("MemberList");
                    if (memberlist.length() == 0) {
                        loading.setText("");
                        tv3.setText("No Members in Module!");
                    } else {
                        sprintMemberList = new ArrayList<Member>();
                        for (int j = 0; j < memberlist.length(); j++) {
                            m = new Member();
                            JSONObject object = memberlist.getJSONObject(j);
                            String mId = object.getString("MemberId");
                            String time = object.getString("TotalTimeSpent");
                            String name = object.getString("MemberName");
                            m.setId(mId);
                            m.setTimeSpent(time);
                            m.setName(name, " ");
                            for (Member member : allMemberList) {
                                if (mId.equals(member.getId())) {
                                    m.setDesig(member.getDesig());
                                    m.setContact(member.getContact());
                                    m.setEmpId(member.getEmpId());
                                    break;
                                }
                            }
                            sprintMemberList.add(m);
                        }
                        new MoreDetailsTask().execute(sprintMemberList);
                    }
                } else
                    Toast.makeText(getContext(), "Error loading records!", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class MoreDetailsTask extends AsyncTask<ArrayList<Member>, String, String> {
        @Override
        protected String doInBackground(ArrayList<Member>... params) {
            String result;
            baseURL = apiConfiguration.getApi();
            infoAddedList = new ArrayList<>();
            for (Member m : params[0]) {
                url = baseURL + "SprintMemberAssociationAPI/GetMySprintList/" + m.getId();
                try {
                    result = httpRequestProcessor.gETRequestProcessor(url);
                    JSONObject jsonObject = new JSONObject(result);
                    Member member = new Member();
                    JSONArray responseData = jsonObject.getJSONArray("responseData");
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        for (int i = 0; i < responseData.length(); i++) {
                            JSONObject object = responseData.getJSONObject(i);
                            String sid = object.getString("SprintId");
                            if (sid.equals(module.getMno())) {
                                member.setId(m.getId());
                                member.setName(m.getName(), "");
                                member.setEmpId(m.getEmpId());
                                member.setContact(m.getContact());
                                member.setDesig(m.getDesig());
                                member.setTimeSpent(m.getTimeSpent());
                                member.setAssociationId(object.getString("SprintMemberAssociationId"));
                                member.setStatus(object.getString("Status"));
                                infoAddedList.add(member);
                                break;
                            }
                        }
                    } else
                        Toast.makeText(getContext(), "Some Error Occured!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.setText("");
            update(infoAddedList);
        }
    }

    private void update(ArrayList<Member> ml) {
        infoAddedList = ml;
        adapter = new ModuleMemberAdapter(getContext(), infoAddedList);
        lv3.setAdapter(adapter);
    }
}