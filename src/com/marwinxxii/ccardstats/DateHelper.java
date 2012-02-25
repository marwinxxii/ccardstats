package com.marwinxxii.ccardstats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public abstract class DateHelper {
    
    //shouldn't be static because of app lifecycle?
    private static GregorianCalendar calendar = new GregorianCalendar();
    private static SimpleDateFormat sberbankDateFormat =
            new SimpleDateFormat("dd.MM.yy HH:mm");
    private static long firstDayOfMonth, firstDayOfNextMonth;
    
    static {
        GregorianCalendar temp = new GregorianCalendar(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        firstDayOfMonth = temp.getTimeInMillis();
        temp.add(Calendar.MONTH, 1);
        firstDayOfNextMonth = temp.getTimeInMillis();
    }
    
    public static long parseSberbankDate(String date) {
        if (date.indexOf("MSK") != -1) {
            date = date.replace("MSK", "").trim();
        }
        try {
            return sberbankDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            return calendar.getTimeInMillis();
        }
    }
    
    public static boolean isInCurrentMonth(long date) {
        return date >= firstDayOfMonth && date < firstDayOfNextMonth;
    }
}
