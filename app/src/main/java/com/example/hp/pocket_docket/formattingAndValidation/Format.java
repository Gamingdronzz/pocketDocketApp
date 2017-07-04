package com.example.hp.pocket_docket.formattingAndValidation;

/**
 * Created by hp on 17-06-2017.
 */

public class Format {

    //This function formats date String
    public static String formatDate(String date) {
        String[] result = removeTime(date).split("-");
        if (result.length == 3) {
            String yr = result[0];
            String mon = result[1];
            String day = result[2];
            return (day + "/" + mon + "/" + yr);
        } else
            return removeTime(date);
    }

    public static String FirstLetterCaps(String s) {
        if (!s.equals(""))
            return (s.substring(0, 1).toUpperCase() + s.substring(1));
        else
            return null;
    }
    public static String removeTime(String date)
    {
        String[] res = date.split("T");
        return res[0];
    }
}
