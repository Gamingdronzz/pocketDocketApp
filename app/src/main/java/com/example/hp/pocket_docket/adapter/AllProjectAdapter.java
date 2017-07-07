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
import com.example.hp.pocket_docket.formattingAndValidation.Validator;

import java.util.ArrayList;

/**
 * Created by hp on 07-07-2017.
 */

public class AllProjectAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Project> projectlist;
    private LayoutInflater inflater;

    public AllProjectAdapter(Context context, ArrayList<Project> projectlist) {
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
        convertView = inflater.inflate(R.layout.singlerow_all_list, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView lang = (TextView) convertView.findViewById(R.id.lang);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        Project p = projectlist.get(position);
        name.setText(p.getTitle());
        lang.setText(p.getType());
        if (Validator.checkEnded(p.getEnd()))
            image.setImageResource(R.mipmap.done);
        else
            image.setImageResource(R.mipmap.active);

        return convertView;
    }


}
