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
import com.example.hp.pocket_docket.beans.Project;
import com.example.hp.pocket_docket.formattingAndValidation.Format;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 03-07-2017.
 */

public class AddModuleFragment extends Fragment {
    private HTTPRequestProcessor httpRequestProcessor;
    private APIConfiguration apiConfiguration;
    private String baseURL, url1, jsonPostString, jsonResponseString;
    private EditText edttitle, edtdescription;
    private TextView start, end, proName;
    private Button btn;
    private String mTitle, mDesc, mStart, mEnd, id, pStart, pEnd;
    private Project p1;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_module, container, false);
        proName = (TextView) view.findViewById(R.id.proView);
        edttitle = (EditText) view.findViewById(R.id.addModuleTitle);
        edtdescription = (EditText) view.findViewById(R.id.addModuleDesc);
        end = (TextView) view.findViewById(R.id.addModuleEnd);
        start = (TextView) view.findViewById(R.id.addModuleStart);
        btn = (Button) view.findViewById(R.id.btnAddModule);

        Bundle bundle = this.getArguments();
        p1 = bundle.getParcelable("Project");
        proName.setText(proName.getText() + "\t" + p1.getTitle());
        /*fab= (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
*/

        httpRequestProcessor = new HTTPRequestProcessor();
        apiConfiguration = new APIConfiguration();
        baseURL = apiConfiguration.getApi();
        url1 = baseURL + "SprintAPI/AddNewSprint";

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE", 4);
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }

        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE", 5);
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }

        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Network.isNetworkAvailable(getContext())) {
                    id = p1.getId();
                    pStart = p1.getStart();
                    pEnd = p1.getEnd();
                    mTitle = edttitle.getText().toString();
                    mDesc = edtdescription.getText().toString();
                    mStart = start.getText().toString();
                    mEnd = end.getText().toString();

                    if (Validator.isEmpty(edttitle)) {
                        edttitle.setError("Module Name Required");
                    } else if (!Validator.isValidModuleStart(mStart, pStart, pEnd))
                        Toast.makeText(getContext(), "Invalid Start Date", Toast.LENGTH_LONG).show();
                    else if (!Validator.isValidModuleEnd(mEnd, mStart, pEnd))
                        Toast.makeText(getContext(), "Invalid End Date", Toast.LENGTH_LONG).show();
                    else {
                        if (mDesc.equals(""))
                            mDesc = "N/A";
                        new AddModule().execute(id, mTitle, mDesc, mStart, mEnd);
                    }
                } else
                    Toast.makeText(getContext(), "Unable to Add! No Internet", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public class AddModule extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Title", Format.FirstLetterCaps(params[1]));
                jsonObject.put("Description", Format.FirstLetterCaps(params[2]));
                jsonObject.put("StartDate", params[3]);
                jsonObject.put("EndDate", params[4]);
                jsonObject.put("ProjectId", params[0]);
                jsonObject.put("Status", "1");
                jsonPostString = jsonObject.toString();
                jsonResponseString = httpRequestProcessor.pOSTRequestProcessor(jsonPostString, url1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonResponseString;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int responseData = jsonObject.getInt("responseData");
                if (responseData == 1) {
                    Toast.makeText(getContext(), "Module Added to Project", Toast.LENGTH_LONG).show();
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
