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
import com.example.hp.pocket_docket.beans.Module;
import com.example.hp.pocket_docket.beans.Project;
import com.example.hp.pocket_docket.formattingAndValidation.Format;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;

import org.json.JSONException;
import org.json.JSONObject;

public class EditModuleFragment extends Fragment {
    private HTTPRequestProcessor httpRequestProcessor;
    private APIConfiguration apiConfiguration;
    private String baseURL, url1, jsonPostString, jsonResponseString;
    private EditText edttitle, edtdescription;
    private TextView start, end, project, duration;
    private Button btn;
    private String mTitle, mDesc, mStart, mEnd;
    private Project p;
    private Module m;
    private boolean enabled;
    private FloatingActionButton fab;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_edit_module, container, false);

        project = (TextView) view.findViewById(R.id.proView);
        duration = (TextView) view.findViewById(R.id.proDuration);
        edttitle = (EditText) view.findViewById(R.id.addModuleTitle);
        edtdescription = (EditText) view.findViewById(R.id.addModuleDesc);
        end = (TextView) view.findViewById(R.id.addModuleEnd);
        start = (TextView) view.findViewById(R.id.addModuleStart);
        btn = (Button) view.findViewById(R.id.btnAddModule);
       /* fab= (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
*/
        httpRequestProcessor = new HTTPRequestProcessor();
        apiConfiguration = new APIConfiguration();
        baseURL = apiConfiguration.getApi();
        url1 = baseURL + "SprintAPI/AddNewSprint";

        Bundle bundle=this.getArguments();
        p = bundle.getParcelable("Project");
        m = bundle.getParcelable("Module");
        project.setText(project.getText() + p.getTitle());
        duration.setText(duration.getText() + Format.formatDate(p.getStart()) + "-" + Format.formatDate(p.getEnd()));
        edttitle.setText(m.getMtitle());
        edtdescription.setText(m.getMdesc());
        start.setText(Format.removeTime(m.getMstart()));
        end.setText(Format.removeTime(m.getMend()));

        enabled = !Validator.checkStarted(m.getMstart());
        start.setEnabled(enabled);

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
                    mTitle = edttitle.getText().toString();
                    mDesc = edtdescription.getText().toString();
                    mStart = start.getText().toString();
                    mEnd = end.getText().toString();

                    if (Validator.isEmpty(edttitle)) {
                        edttitle.setError("Module Name Required");
                    } else if (start.isEnabled() && !Validator.isValidModuleStart(mStart, p.getStart(), m.getMstart()))
                        Toast.makeText(getContext(), "Invalid Start Date", Toast.LENGTH_LONG).show();
                    else if (!Validator.isValidModuleEnd(mEnd, m.getMend(), p.getEnd()))
                        Toast.makeText(getContext(), "Invalid End Date", Toast.LENGTH_LONG).show();
                    else {
                        if (mDesc.equals(""))
                            mDesc = "N/A";
                        new EditModule().execute(p.getId(), mTitle, mDesc, mStart, mEnd, m.getStatus(), m.getMno());
                    }
                } else
                    Toast.makeText(getContext(), "Unable to Add! No Internet", Toast.LENGTH_LONG).show();

            }
        });
        
        return view;
    }
    private class EditModule extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Title", Format.FirstLetterCaps(params[1]));
                jsonObject.put("Description", Format.FirstLetterCaps(params[2]));
                jsonObject.put("StartDate", params[3]);
                jsonObject.put("EndDate", params[4]);
                jsonObject.put("ProjectId", params[0]);
                jsonObject.put("Status", params[5]);
                jsonObject.put("SprintId", params[6]);
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
                    Toast.makeText(getContext(), "Module Updated", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error Updating Module!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
  
}
