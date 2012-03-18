package com.github.marwinxxii.ccardstats.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public abstract class DateHelper {

    // shouldn't be static because of app lifecycle?
    private static GregorianCalendar calendar = new GregorianCalendar();
    private static SimpleDateFormat sberbankDateFormat = new SimpleDateFormat(
            "dd.MM.yy HH:mm");
    private static long firstDayOfMonth, firstDayOfNextMonth;
    private static long today, tomorrow;
    public static int year, month, day;
    public static Date Today;
    private static String[] monthNames;

    static {
        GregorianCalendar temp = new GregorianCalendar(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        firstDayOfMonth = temp.getTimeInMillis();
        temp.add(Calendar.MONTH, 1);
        firstDayOfNextMonth = temp.getTimeInMillis();
        temp.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        temp.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        today = temp.getTimeInMillis();
        temp.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow = temp.getTimeInMillis();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Today = calendar.getTime();
    }
    
    public static void setMonthNames(String[] names) {
        monthNames = names;
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

    public static boolean isToday(long date) {
        return date >= today && date < tomorrow;
    }

    public static long getToday() {
        return today;
    }

    public static long getTomorrow() {
        return tomorrow;
    }
    
    public static String getMonthName(int month) {
        return monthNames[month-1];
    }
}
