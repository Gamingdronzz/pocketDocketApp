package com.example.hp.pocket_docket.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.beans.Module;
import com.example.hp.pocket_docket.formattingAndValidation.Format;
import com.example.hp.pocket_docket.formattingAndValidation.Validator;
import com.example.hp.pocket_docket.fragments.ModuleDetailFragment;

import java.util.ArrayList;

/**
 * Created by hp on 18-05-2017.
 */

public class ModuleAdapter extends BaseAdapter {
    Context context;
    private ArrayList<Module> modulelist;
    private LayoutInflater inflater;
    Bundle bundle = new Bundle();

    public ModuleAdapter(Context context, ArrayList<Module> modulelist) {
        super();
        this.context = context;
        this.modulelist = modulelist;
    }

    @Override
    public int getCount() {
        return modulelist.size();
    }

    @Override
    public Object getItem(int position) {
        return modulelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.singlerow_module, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.modName);
        TextView descp = (TextView) convertView.findViewById(R.id.modDescp);
        TextView duration = (TextView) convertView.findViewById(R.id.modDuration);
        ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.determinateBar);
        ImageView high= (ImageView) convertView.findViewById(R.id.priority);

        Module m = modulelist.get(position);
        name.setText(m.getMtitle());
        if (Validator.endNear(m.getMend()))
            high.setImageResource(R.mipmap.high);
        if(Validator.checkEnded(m.getMend()))
            high.setImageResource(R.mipmap.done);
        switch (Integer.valueOf(m.getStatus())) {
            case 1:
                pb.setProgress(10);
                break;
            case 2:
                pb.setProgress(35);
                break;
            case 3:
                pb.setProgress(75);
                break;
            case 4:
                pb.setProgress(100);
                break;
            default:
                pb.setProgress(0);

        }
        descp.setText(m.getMdesc());
        duration.setText(Format.formatDate(m.getMstart()) + " - " + Format.formatDate(m.getMend()));

        Button detail = (Button) convertView.findViewById(R.id.btnDetail);
        detail.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if (parent instanceof AdapterView) {
                                              final AdapterView adapterView = (AdapterView) parent;
                                              final int pos = adapterView.getPositionForView(v);
                                              Module m = modulelist.get(pos);
                                              bundle.putParcelable("Module",m);
                                          }
                                          Fragment f2 = new ModuleDetailFragment();
                                          f2.setArguments(bundle);
                                          FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                                          FragmentTransaction ftc = fm.beginTransaction();
                                          ftc.replace(R.id.content_admin_dashboard, f2);
                                          ftc.commit();
                                          ftc.addToBackStack(null);
                                      }
                                  }

        );
        return convertView;
    }


}
