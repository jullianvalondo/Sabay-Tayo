package com.entry.globelabs.hacktayo.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by JettRobin on 2/5/2016.
 */
public class DateUtils {


    private static String DATE_FORMAT_LONG = "MMMM dd, yyyy";
    private static String DATE_FORMAT_SHORT = "MMM dd, yyyy";
    private static String DATE_FORMAT_SLASH = "MM/dd/yyyy";
    private static String DATE_FORMAT_DASH = "MM-dd-yyyy";

    private static Calendar calendar = Calendar.getInstance();

    public static String getStringFormatSlash(Date date){
        String formattedDate = new SimpleDateFormat(DATE_FORMAT_SLASH).format(date);
        return formattedDate;
    }

    public static String getStringFormatShort(Date date){
        String formattedDate = new SimpleDateFormat(DATE_FORMAT_SHORT).format(date);
        return formattedDate;
    }

    public static String getStringFormatLong(Date date){
        String formattedDate = new SimpleDateFormat(DATE_FORMAT_LONG).format(date);
        return formattedDate;
    }

    public static String getStringFormatDash(Date date){
        String formattedDate = new SimpleDateFormat(DATE_FORMAT_DASH).format(date);
        return formattedDate;
    }

    public static String getStringFormatCustom(Date date, String stringFormat){
        String formattedDate = new SimpleDateFormat(stringFormat).format(date);
        return formattedDate;
    }

    public static Date getDateWithoutTime(Date date){
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Boolean dateIsSameToday(Date date1){
        Date currentDate = Calendar.getInstance().getTime();
        Date target = getDateWithoutTime(date1);

        int dateMargin = date1.compareTo(currentDate);

        if(dateMargin == 0) {
            return true;
        }

        return false;
    }
}
