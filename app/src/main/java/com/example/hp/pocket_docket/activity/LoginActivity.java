package com.example.hp.pocket_docket.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;
import com.example.hp.pocket_docket.networkConnection.Network;
import com.example.hp.pocket_docket.shared_preferences.SavedSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText edtname, edtpassword;
    private Button btnLogin;
    private TextView tv;
    private String name, passwd;
    private HTTPRequestProcessor req;
    private APIConfiguration api;
    private String baseURL, urlLogin, urlForgot, jsonStringToPost, jsonResponseString;
    private boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //find views
        btnLogin = (Button) findViewById(R.id.btnLogin);
        edtname = (EditText) findViewById(R.id.edtLoginName);
        edtpassword = (EditText) findViewById(R.id.edtLoginPass);
        tv = (TextView) findViewById(R.id.forgot);

        req = new HTTPRequestProcessor();
        api = new APIConfiguration();
        baseURL = api.getApi();
        urlLogin = baseURL + "AccountAPI/GetLoginUser";
        urlForgot = baseURL + "MemberAPI/ForgotPassword";

        //Forgot Password
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                View promptsView = li.inflate(R.layout.forgot_password, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        LoginActivity.this);

                // set forgot_password.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.edtUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String mail = userInput.getText().toString();
                                        new ForgotTask().execute(mail);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });

        //Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = edtname.getText().toString().trim();
                passwd = edtpassword.getText().toString().trim();
                if (!Network.isNetworkAvailable(LoginActivity.this))                       //check internet connectivity
                    Toast.makeText(LoginActivity.this, "Please connect to internet ", Toast.LENGTH_LONG).show();
                else
                    new LoginTask().execute(name, passwd);          //login if connected

            }
        });

    }
    protected void onPause() {
        super.onPause();
        finish();
    }


    //-------------------------------------------- LOGIN TASK------------------------------------------------
    public class LoginTask extends AsyncTask<String, String, String> {

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            name = params[0];
            passwd = params[1];

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("UserName", name);
                jsonObject.put("Password", passwd);
                jsonStringToPost = jsonObject.toString();
                jsonResponseString = req.pOSTRequestProcessor(jsonStringToPost, urlLogin);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonResponseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                edtname.setText(" ");
                edtpassword.setText("");
                pd.dismiss();
                JSONObject jsonObject = new JSONObject(s);
                success = jsonObject.getBoolean("success");
                String msg=jsonObject.getString("ErrorMessage");
                if (success) {
                    String myName= jsonObject.getString("FName") + " " + jsonObject.get("LName");
                    String typeId = jsonObject.getString("UserTypeId");
                    String code = jsonObject.getString("UserIdentityKey");
                    SavedSharedPreference.setName(LoginActivity.this,myName);
                    SavedSharedPreference.setUserName(LoginActivity.this,name);
                    SavedSharedPreference.setType(LoginActivity.this,typeId);
                    SavedSharedPreference.setCode(LoginActivity.this,code);
                    if (typeId.equals("1")) {
                        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent1 = new Intent(LoginActivity.this, DeveloperDashboardActivity.class);
                        startActivity(intent1);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //-------------------------------------------- Forgot password TASK------------------------------------------------
    public class ForgotTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String userName = params[0];
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("UserName", userName);
                jsonStringToPost = jsonObject.toString();
                jsonResponseString = req.pOSTRequestProcessor(jsonStringToPost, urlForgot);
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
                int responseData = jsonObject.getInt("responseData");
                success = jsonObject.getBoolean("success");
                if (success) {
                    if (responseData == 1) {
                        Toast.makeText(LoginActivity.this, "Password sent to your Mailbox", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Please provide a Registered UserName!", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}