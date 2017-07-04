package com.example.hp.pocket_docket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hp.pocket_docket.R;

import java.util.ArrayList;


/**
 * Created by admin on 4/26/2017.
 */

public class MemberAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<com.example.hp.pocket_docket.beans.Member> memberlist;
    private LayoutInflater inflater;

    public MemberAdapter(Context context, ArrayList<com.example.hp.pocket_docket.beans.Member> memberlist) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.singlerow_member, parent, false);

        TextView tname = (TextView) convertView.findViewById(R.id.txtname);
        TextView tid= (TextView) convertView.findViewById(R.id.txtid);
        TextView design = (TextView) convertView.findViewById(R.id.designation);
        TextView gender = (TextView) convertView.findViewById(R.id.gender);
        TextView email = (TextView) convertView.findViewById(R.id.email);
        TextView mob = (TextView) convertView.findViewById(R.id.mob);

        com.example.hp.pocket_docket.beans.Member m = memberlist.get(position);
        tname.setText(m.getName());
        tid.setText(m.getEmpId());
        design.setText(design.getText() + " " + m.getDesig());
        gender.setText(gender.getText() + " " + m.getGender());
        email.setText(email.getText() + " " + m.getEmail());
        mob.setText(mob.getText() + " " + m.getContact());

        return convertView;
    }
}
