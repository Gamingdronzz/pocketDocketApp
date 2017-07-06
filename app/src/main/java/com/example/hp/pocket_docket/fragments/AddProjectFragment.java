package com.example.hp.pocket_docket.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.hp.pocket_docket.formattingAndValidation.Format;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 03-07-2017.
 */

public class AddProjectFragment extends Fragment {
    private HTTPRequestProcessor httpRequestProcessor;
    private APIConfiguration apiConfiguration;
    private String jsonPostString, jsonResponseString, baseURL, url;
    private EditText title, description, type;
    private TextView start, end;
    private Button btn;
    private String pTitle, pDesc, pStart, pEnd, pType;
    boolean success;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_project, container, false);
        title = (EditText) view.findViewById(R.id.newProjectTitle);
        type = (EditText) view.findViewById(R.id.newProjectType);
        description = (EditText) view.findViewById(R.id.newProjectDesc);
        start = (TextView) view.findViewById(R.id.newProjectStart);
        end = (TextView) view.findViewById(R.id.newProjectEnd);
        btn = (Button) view.findViewById(R.id.btnNewProject);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        httpRequestProcessor = new HTTPRequestProcessor();
        apiConfiguration = new APIConfiguration();

        baseURL = apiConfiguration.getApi();
        url = baseURL + "ProjectAPI/AddNewProject";

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
                    pType = type.getText().toString();
                    pDesc = description.getText().toString();
                    pStart = start.getText().toString();
                    pEnd = end.getText().toString();

                    if (Validator.isEmpty(title)) {
                        title.setError("Project Name Required");
                    } else if (Validator.isEmpty(type)) {
                        type.setError("Project Type Required");
                    } else if (!Validator.isValidProjectStart(pStart))
                        Toast.makeText(getContext(), "Invalid Start Date", Toast.LENGTH_LONG).show();
                    else if (!Validator.isValidProjectEnd(pEnd, pStart))
                        Toast.makeText(getContext(), "Invalid End Date", Toast.LENGTH_LONG).show();
                    else {
                        if (pDesc.equals(""))
                            pDesc = "N/A";
                        new AddProject().execute(pTitle, pDesc, pStart, pEnd, pType);
                    }
                } else
                    Toast.makeText(getContext(), "Unable to Add! No Internet", Toast.LENGTH_LONG).show();

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        fab.setVisibility(View.VISIBLE);
    }

    public class AddProject extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            pTitle = Format.FirstLetterCaps(params[0]);
            pDesc = Format.FirstLetterCaps(params[1]);
            pStart = params[2];
            pEnd = params[3];
            pType = Format.FirstLetterCaps(params[4]);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Title", pTitle);
                jsonObject.put("ProjectType", pType);
                jsonObject.put("Description", pDesc);
                jsonObject.put("StartDate", pStart);
                jsonObject.put("EndDate", pEnd);
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
                success = jsonObject.getBoolean("success");
                int responseData = jsonObject.getInt("responseData");
                if (responseData == 1) {
                    Toast.makeText(getContext(), "Project Added", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Record already exists", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
