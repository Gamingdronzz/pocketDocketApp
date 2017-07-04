package com.example.hp.pocket_docket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.beans.Module;
import com.example.hp.pocket_docket.formattingAndValidation.Format;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;

import java.util.ArrayList;

/**
 * Created by hp on 21-05-2017.
 */

public class MyModuleAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Module> al;
    private LayoutInflater inflater;

    @Override
    public int getCount() {
        return al.size();
    }

    @Override
    public Object getItem(int position) {
        return al.get(position);
    }

    public MyModuleAdapter(Context context, ArrayList<Module> al) {
        this.context = context;
        this.al = al;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.singlerow_my_list, parent, false);

        TextView startDate = (TextView) convertView.findViewById(R.id.modStart);
        TextView endDate = (TextView) convertView.findViewById(R.id.modEnd);
        TextView moduleName = (TextView) convertView.findViewById(R.id.myModName);
        TextView moduleDescription = (TextView) convertView.findViewById(R.id.myModDesc);
        TextView projectName = (TextView) convertView.findViewById(R.id.myProName);
        TextView type = (TextView) convertView.findViewById(R.id.technology);
        TextView time = (TextView) convertView.findViewById(R.id.totalTimeSpent);
        ImageView high = (ImageView) convertView.findViewById(R.id.priority);

        Module m = al.get(position);
        startDate.setText(Format.formatDate(m.getMstart()));
        endDate.setText(Format.formatDate(m.getMend()));
        if (Validator.endNear(m.getMend()))
            high.setImageResource(R.mipmap.high);
        moduleName.setText(moduleName.getText() + " " + m.getMtitle());
        moduleDescription.setText(m.getMdesc());
        projectName.setText(projectName.getText() + " " + m.getTitle());
        time.setText(m.getTotalTime());
        type.setText(m.getType());
        return convertView;

    }
}
