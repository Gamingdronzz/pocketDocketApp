package com.example.hp.pocket_docket.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.apiConfiguration.APIConfiguration;
import com.example.hp.pocket_docket.beans.Member;
import com.example.hp.pocket_docket.httpRequestProcessor.HTTPRequestProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 14-06-2017.
 */

public class ModuleMemberAdapter extends BaseAdapter {

    Context context;
    Member m;
    private String baseURL, url, res;
    private HTTPRequestProcessor httpRequestProcessor = new HTTPRequestProcessor();
    private APIConfiguration apiConfiguration = new APIConfiguration();
    private ArrayList<Member> memberlist;
    private LayoutInflater inflater;

    public ModuleMemberAdapter(Context context, ArrayList<Member> memberlist) {
        this.context = context;
        this.memberlist = memberlist;
    }

    @Override
    public int getCount() {
        return memberlist.size();
    }

    @Override
    public Object getItem(int position) {
        return memberlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.singlerow_module_members, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.empName);
        TextView desig = (TextView) convertView.findViewById(R.id.empDesig);
        TextView timeSpent = (TextView) convertView.findViewById(R.id.timeSpent);
        ProgressBar status = (ProgressBar) convertView.findViewById(R.id.empStatus);
        ImageButton call = (ImageButton) convertView.findViewById(R.id.call);
        ImageButton msg = (ImageButton) convertView.findViewById(R.id.msg);
        final ImageButton delete = (ImageButton) convertView.findViewById(R.id.delete);

        m = memberlist.get(position);
        name.setText(m.getEmpId() + "   " + m.getName());
        desig.setText(m.getDesig());
        switch (Integer.valueOf(m.getStatus())) {
            case 1:
                status.setProgress(10);
                break;
            case 2:
                status.setProgress(35);
                break;
            case 3:
                status.setProgress(75);
                break;
            case 4:
                status.setProgress(100);
                break;
            default:
                status.setProgress(0);

        }
        String time = m.getTimeSpent();
        int h, min1;
        if (time.equals("null")) {
            h = 0;
            min1 = 0;
        } else {
            h = (int) (Double.valueOf(time) / 60);
            min1 = (int) (Double.valueOf(time) % 60);
        }
        timeSpent.setText(h + " Hr : " + min1 + " Min");

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws SecurityException {
                if (parent instanceof AdapterView) {
                    final AdapterView adapterView = (AdapterView) parent;
                    final int pos = adapterView.getPositionForView(v);
                    m = memberlist.get(pos);
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + m.getContact()));
                    context.startActivity(callIntent);
                }
            }
        });
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent instanceof AdapterView) {
                    final AdapterView adapterView = (AdapterView) parent;
                    final int pos = adapterView.getPositionForView(v);
                    m = memberlist.get(pos);
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + m.getContact()));
                    context.startActivity(smsIntent);
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent instanceof AdapterView) {
                    final AdapterView adapterView = (AdapterView) parent;
                    final int pos = adapterView.getPositionForView(v);
                    m = memberlist.get(pos);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder
                            .setMessage("Are you sure you want to remove " + m.getName() + "from this Module?")
                            .setCancelable(true)
                            .setPositiveButton("YES",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            new DeleteAssociationTask().execute(m.getAssociationId());
                                            memberlist.remove(pos);
                                            notifyDataSetChanged();
                                        }
                                    })
                            .setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            }
        });
        return convertView;
    }

    private class DeleteAssociationTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            baseURL = apiConfiguration.getApi();
            url = baseURL + "SprintMemberAssociationAPI/DeleteSprintAssociation/" + params[0];
            res = httpRequestProcessor.gETRequestProcessor(url);
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    Toast.makeText(context, "Member removed from Module", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Unable to Delete", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Some Error Occured", Toast.LENGTH_LONG).show();
            }
        }
    }
}