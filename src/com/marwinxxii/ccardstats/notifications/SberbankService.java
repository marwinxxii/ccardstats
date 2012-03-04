package com.marwinxxii.ccardstats.notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.marwinxxii.ccardstats.helpers.DateHelper;

public class SberbankService implements NotificationService {

    private static final int INDEX_CARD = 0;
    private static final int INDEX_OPERATION = 1;
    private static final int INDEX_AMOUNT = 3;

    private static SimpleDateFormat sberbankDateFormat = new SimpleDateFormat(
            "dd.MM.yy HH:mm");

    private static final IllegalArgumentException EXCEPTION = new IllegalArgumentException();

    @Override
    public String getAddress() {
        return "900";
        // return "+79215677256";
    }

    @Override
    public SmsNotification recognise(String body)
            throws IllegalArgumentException {
        if (body.indexOf(';') == -1)
            throw EXCEPTION;
        String[] fields = body.toLowerCase().split(";");
        Date date = parseDate(fields[fields.length - 2]);
        double balance = removeCurrency(fields[fields.length - 1].replace(
                "dostupno:", ""));
        double diff = removeCurrency(fields[INDEX_AMOUNT].replace("summa:", ""));
        if (!fields[INDEX_OPERATION].trim().equals("popolnenie scheta")) {
            diff = -diff;
        }
        return new SmsNotification(fields[INDEX_CARD], diff, balance,
                date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }

    public static Date parseDate(String date) {
        if (date.indexOf("msk") != -1) {
            date = date.replace("msk", "");
        }
        date = date.trim();
        try {
            return sberbankDateFormat.parse(date);
        } catch (ParseException e) {
            return DateHelper.Today;
        }
    }

    private static double removeCurrency(String number) {
        int curStart = number.indexOf('.') + 2;
        return Double.valueOf(number.substring(0, curStart));
    }

}
