package com.example.hp.pocket_docket.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.formattingAndValidation.Format;
import com.example.hp.pocket_docket.fragments.DatePickerFragment;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterMembersActivity extends AppCompatActivity {


    private Button btnRegister;
    private EditText edtFName, edtLName, edtMail, edtContact, edtUserName, edtPass, edtReEnter, edtId;
    private TextView txtdob;
    private Spinner sp1, sp2;
    private String fname, lname, emailID, phone, userName, password, dob, gender, desig, reEnter, empId;
    private HTTPRequestProcessor httpRequestProcessor;
    private APIConfiguration apiConfiguration;
    private String baseURL, urlRegister;
    private String jsonPostString, jsonResponseString;
    private int success;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_members);

        //find views

        edtFName = (EditText) findViewById(R.id.edtRegisterFirstName);
        edtLName = (EditText) findViewById(R.id.edtRegisterLastName);
        edtId = (EditText) findViewById(R.id.edtRegisterID);
        sp2 = (Spinner) findViewById(R.id.spinRegisterDesig);
        edtMail = (EditText) findViewById(R.id.edtRegisterMail);
        edtContact = (EditText) findViewById(R.id.edtRegisterContact);
        txtdob = (TextView) findViewById(R.id.txtRegisterDob);
        edtUserName = (EditText) findViewById(R.id.edtRegisterUserName);
        edtPass = (EditText) findViewById(R.id.edtRegisterPass);
        edtReEnter = (EditText) findViewById(R.id.edtRegisterPass2);
        sp1 = (Spinner) findViewById(R.id.spinRegisterGender);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        //Initialization
        httpRequestProcessor = new HTTPRequestProcessor();
        apiConfiguration = new APIConfiguration();
        baseURL = apiConfiguration.getApi();
        urlRegister = baseURL + "AccountAPI/SaveApplicationUser";

        txtdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE", 3);
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Network.isNetworkAvailable(RegisterMembersActivity.this)) {
                    //get values
                    fname = edtFName.getText().toString();
                    lname = edtLName.getText().toString();
                    empId = edtId.getText().toString();
                    desig = sp2.getSelectedItem().toString();
                    emailID = edtMail.getText().toString();
                    phone = edtContact.getText().toString();
                    gender = sp1.getSelectedItem().toString();
                    dob = txtdob.getText().toString();
                    userName = edtUserName.getText().toString();
                    password = edtPass.getText().toString();
                    reEnter = edtReEnter.getText().toString();

                    //validations
                    if (Validator.isEmpty(edtFName)) {
                        edtFName.setError("First Name Required");
                    } else if (Validator.isEmpty(edtLName)) {
                        edtLName.setError("Last Name Required");
                    } else if (Validator.isEmpty(edtId))
                        edtId.setError("ID Required");
                    else if (!Validator.isValidDOB(dob)) {
                        Toast.makeText(RegisterMembersActivity.this, "Please provide a valid BirthDate", Toast.LENGTH_LONG).show();
                    } else if (!Validator.isValidPhone(phone)) {
                        edtContact.setError("Invalid Contact Number");
                    } else if (!Validator.isValidEmail(emailID)) {
                        edtMail.setError("Invalid Email");
                    } else if (Validator.isEmpty(edtUserName)) {
                        edtUserName.setError("UserName Required");
                    } else if (!Validator.isValidPassword(password)) {
                        edtPass.setError("Invalid Password. Must be 5 or more Characters");
                    } else if (!(password.equals(reEnter))) {
                        edtReEnter.setText("");
                        edtReEnter.requestFocus();
                        Toast.makeText(RegisterMembersActivity.this, "Password Mismatch! ReEnter Password.", Toast.LENGTH_LONG).show();
                    } else {
                        new RegistrationTask().execute(fname, lname, desig, gender, dob, emailID, phone, userName, password,empId);
                    }
                } else
                    Toast.makeText(RegisterMembersActivity.this, "Unable to Register! No Internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    public class RegistrationTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            fname = Format.FirstLetterCaps(params[0]);
            lname = Format.FirstLetterCaps(params[1]);
            desig = params[2];
            gender = params[3];
            dob = params[4];
            emailID = params[5];
            phone = params[6];
            userName = params[7];
            password = params[8];
            empId=params[9];

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("FName", fname);
                jsonObject.put("LName", lname);
                jsonObject.put("MemberCode",empId);
                jsonObject.put("Designation", desig);
                jsonObject.put("Gender", gender);
                jsonObject.put("DateOfBirth", dob);
                jsonObject.put("EmailId", emailID);
                jsonObject.put("MobileNo", phone);
                jsonObject.put("UserTypeId", 4);
                jsonObject.put("UserName", userName);
                jsonObject.put("Password", password);

                jsonPostString = jsonObject.toString();
                jsonResponseString = httpRequestProcessor.pOSTRequestProcessor(jsonPostString, urlRegister);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonResponseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                success = jsonObject.getInt("responseData");
                if (success == 1) {
                    Toast.makeText(RegisterMembersActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterMembersActivity.this);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setMessage("Share Login Credentials with " + fname + " " + lname)
                            .setPositiveButton("Mail",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                            emailIntent.setType("text/plain");
                                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailID}); // recipients
                                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Login credentials");
                                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello " + fname + "\nKindly install Pocket Docket Application on your android Mobile." +
                                                    " Use the following Credentials to Login to the app.\nUsername: " + userName + "\nPassword: " + password);
                                            startActivity(emailIntent);
                                            finish();
                                        }
                                    })
                            .setNegativeButton("SMS",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
                                            smsIntent.putExtra("sms_body", "Hello " + fname + "\nUse the following Credentials to Login to pocket docket app.\nUsername: " + userName + "\nPassword: " + password);
                                            startActivity(smsIntent);
                                            finish();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(RegisterMembersActivity.this, "UserName already Exists!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}


