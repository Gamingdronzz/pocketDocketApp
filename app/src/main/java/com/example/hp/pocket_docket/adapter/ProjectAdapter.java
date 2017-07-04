package com.example.hp.pocket_docket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.beans.Project;
import com.example.hp.pocket_docket.formattingAndValidation.Format;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;

import java.util.ArrayList;

/**
 * Created by hp on 15-05-2017.
 */

public class ProjectAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Project> projectlist;
    private LayoutInflater inflater;

    public ProjectAdapter(Context context, ArrayList<Project> projectlist) {
        this.context = context;
        this.projectlist = projectlist;

    }

    @Override
    public int getCount() {
        return projectlist.size();
    }

    @Override
    public Object getItem(int position) {
        return projectlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.singlerow_project, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.proName);
        TextView lang = (TextView) convertView.findViewById(R.id.proTech);
        TextView desc = (TextView) convertView.findViewById(R.id.proDesc);
        TextView start = (TextView) convertView.findViewById(R.id.proStartDate);
        TextView end = (TextView) convertView.findViewById(R.id.proEndDate);
        ImageView high = (ImageView) convertView.findViewById(R.id.priority);

        Project p = projectlist.get(position);
        name.setText(p.getTitle());
        desc.setText(p.getDesc());
        lang.setText(p.getType());
        start.setText(Format.formatDate(p.getStart()));
        end.setText(Format.formatDate(p.getEnd()));
        if (Validator.endNear(p.getEnd()))
            high.setImageResource(R.mipmap.high);

        return convertView;
    }


}
