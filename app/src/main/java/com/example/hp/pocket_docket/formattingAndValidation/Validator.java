package com.example.hp.pocket_docket.formattingAndValidation;

import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hp on 30-05-2017.
 */

public class Validator {

    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    public static String todayDate = dateFormatter.format(new Date());

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPhone(String phone) {
        String NUMBER_PATTERN = "^([7-9]{1})([0-9]{9})$";
        Pattern pattern = Pattern.compile(NUMBER_PATTERN);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 5) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(EditText edt) {
        if (edt.getText().toString().length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isValidDOB(String date) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int year = c.get(Calendar.YEAR);
        if (!(date.length() == 0)) {
            String[] out = date.split("-");
            int y = Integer.valueOf(out[0]);
            if (year - y >= 21)
                return true;
        }
        return false;
    }

    public static boolean isValidProjectStart(String date) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        if (!date.equals(null)) {
            try {
                if (dateFormatter.parse(date).getTime() >= dateFormatter.parse(todayDate).getTime())
                    return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isValidProjectEnd(String date, String start) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        if (!date.equals(null)) {
            try {
                if (dateFormatter.parse(date).getTime() > dateFormatter.parse(start).getTime())
                    return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isValidModuleStart(String date, String pStart, String pEnd) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        if (!date.equals(null)) {
            try {
                if (dateFormatter.parse(date).getTime() >= dateFormatter.parse(pStart).getTime())
                    if (dateFormatter.parse(date).getTime() <= dateFormatter.parse(pEnd).getTime())
                        return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isValidModuleEnd(String date, String lower, String upper) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        if (!date.equals(null)) {
            try {
                if (dateFormatter.parse(date).getTime() >= dateFormatter.parse(lower).getTime())
                    if (dateFormatter.parse(date).getTime() <= dateFormatter.parse(upper).getTime())
                        return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isValidProEditStart(String date, String oldStart) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        if (!date.equals(null)) {
            try {
                if (dateFormatter.parse(todayDate).getTime() <= dateFormatter.parse(date).getTime()) {
                    if (dateFormatter.parse(date).getTime() <= dateFormatter.parse(oldStart).getTime())
                        return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isValidProEditEnd(String date, String oldEnd) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        if (!date.equals(null)) {
            try {
                if (dateFormatter.parse(oldEnd).getTime() <= dateFormatter.parse(date).getTime()) {
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean checkStarted(String start) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        try {
            if (!start.equals(null)) {
                if (dateFormatter.parse(start).getTime() <= dateFormatter.parse(todayDate).getTime()) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean checkEnded(String end) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        try {
            if (!end.equals(null)) {
                if (dateFormatter.parse(end).getTime() < dateFormatter.parse(todayDate).getTime()) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean endNear(String end) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        try {
            if (dateFormatter.parse(end).getTime() - dateFormatter.parse(todayDate).getTime() <= 1296000000) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }
}