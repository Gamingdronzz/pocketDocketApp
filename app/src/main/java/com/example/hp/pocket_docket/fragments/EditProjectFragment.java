package com.example.hp.pocket_docket.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.beans.Project;
import com.example.hp.pocket_docket.formattingAndValidation.Format;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProjectFragment extends Fragment {

    private HTTPRequestProcessor httpRequestProcessor;
    private APIConfiguration apiConfiguration;
    private String jsonPostString, jsonResponseString, baseURL, url;
    private EditText title, description, type;
    private TextView start, end;
    private Button btn;
    private String pTitle, pDesc, pStart, pEnd;
    private boolean enabled;
    private Project p;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_project, container, false);
        title = (EditText) view.findViewById(R.id.newProjectTitle);
        type = (EditText) view.findViewById(R.id.newProjectType);
        description = (EditText) view.findViewById(R.id.newProjectDesc);
        start = (TextView) view.findViewById(R.id.newProjectStart);
        end = (TextView) view.findViewById(R.id.newProjectEnd);
        btn = (Button) view.findViewById(R.id.btnEditProject);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        httpRequestProcessor = new HTTPRequestProcessor();
        apiConfiguration = new APIConfiguration();
        baseURL = apiConfiguration.getApi();
        url = baseURL + "ProjectAPI/AddNewProject";

        Bundle bundle = this.getArguments();
        p = bundle.getParcelable("Project");
        title.setText(p.getTitle());
        description.setText(p.getDesc());
        type.setText(p.getType());
        type.setEnabled(false);
        start.setText(Format.removeTime(p.getStart()));
        enabled = !Validator.checkStarted(p.getStart());
        start.setEnabled(enabled);
        end.setText(Format.removeTime(p.getEnd()));

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE", 1);
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }

        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE", 2);
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Network.isNetworkAvailable(getContext())) {
                    pTitle = title.getText().toString();
                    pDesc = description.getText().toString();
                    pStart = start.getText().toString();
                    pEnd = end.getText().toString();

                    if (Validator.isEmpty(title)) {
                        title.setError("Project Name Required");
                    } else if (enabled == true && !Validator.isValidProEditStart(pStart, Format.removeTime(p.getStart()))) {
                        Toast.makeText(getContext(), "Invalid Start", Toast.LENGTH_LONG).show();
                    } else if (!Validator.isValidProEditEnd(pEnd, Format.removeTime(p.getEnd()))) {
                        Toast.makeText(getContext(), "Invalid End Date", Toast.LENGTH_LONG).show();
                    } else {
                        if (pDesc.equals(""))
                            pDesc = "N/A";
                        new EditProject().execute(p.getId(), pTitle, pDesc, pStart, pEnd, p.getType());
                    }
                } else
                    Toast.makeText(getContext(), "Unable to Edit! No Internet", Toast.LENGTH_LONG).show();

            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();
        fab.setVisibility(View.INVISIBLE);
    }

    public void onStop() {
        super.onStop();
        fab.setVisibility(View.VISIBLE);
    }

    private class EditProject extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("ProjectId", params[0]);
                jsonObject.put("Title", Format.FirstLetterCaps(params[1]));
                jsonObject.put("Description", Format.FirstLetterCaps(params[2]));
                jsonObject.put("StartDate", params[3]);
                jsonObject.put("EndDate", params[4]);
                jsonObject.put("ProjectType", Format.FirstLetterCaps(params[5]));
                jsonPostString = jsonObject.toString();
                jsonResponseString = httpRequestProcessor.pOSTRequestProcessor(jsonPostString, url);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }
            return jsonResponseString;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int responseData = jsonObject.getInt("responseData");
                if (responseData == 1) {
                    Toast.makeText(getContext(), "Project Updated", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error updating Project!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
