package com.example.hp.pocket_docket.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.hp.pocket_docket.R;

import java.util.Calendar;

/**
 * Created by hp on 10-05-2017.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    static final int START_DATE = 1;
    static final int END_DATE = 2;
    static final int D_O_B = 3;
    static final int MODULE_START = 4;
    static final int MODULE_END = 5;

    private int mChosenDate;

    int cur = 0;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mChosenDate = bundle.getInt("DATE", 1);
        }

        switch (mChosenDate) {
            case START_DATE:
                cur = START_DATE;
                return new DatePickerDialog(getActivity(), this, year, month, day);
            case END_DATE:
                cur = END_DATE;
                return new DatePickerDialog(getActivity(), this, year, month, day);
            case MODULE_START:
                cur = MODULE_START;
                return new DatePickerDialog(getActivity(), this, year, month, day);
            case MODULE_END:
                cur = MODULE_END;
                return new DatePickerDialog(getActivity(), this, year, month, day);
            case D_O_B:
                cur = D_O_B;
                return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        return null;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        month = month + 1;
        String stringOfDate = year + "-" + month + "-" + day;

        if (cur == START_DATE) {
            // set selected date into textview
            TextView date1 = (TextView) getActivity().findViewById(R.id.newProjectStart);
            date1.setText(stringOfDate);
        } else if (cur == END_DATE) {
            TextView date2 = (TextView) getActivity().findViewById(R.id.newProjectEnd);
            date2.setText(stringOfDate);
        } else if (cur == MODULE_START) {
            TextView date3 = (TextView) getActivity().findViewById(R.id.addModuleStart);
            date3.setText(stringOfDate);
        } else if (cur == MODULE_END) {
            TextView date4 = (TextView) getActivity().findViewById(R.id.addModuleEnd);
            date4.setText(stringOfDate);
        } else if (cur == D_O_B) {
            TextView date5 = (TextView) getActivity().findViewById(R.id.txtRegisterDob);
            date5.setText(stringOfDate);
        }

    }
}