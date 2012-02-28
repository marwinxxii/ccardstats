package com.marwinxxii.ccardstats.notifications;

import com.marwinxxii.ccardstats.helpers.DateHelper;

public class SberbankNotification implements SmsNotification {

    public static final int CARD_INDEX = 0;
    public static final int TYPE_INDEX = 1;
    public static final int STATUS_INDEX = 2;
    public static final int AMOUNT_INDEX = 3;

    private String[] fields;

    public SberbankNotification(String[] fields) {
        this.fields = fields;
    }

    public String getCard() {
        return fields[CARD_INDEX];
    }

    public String getType() {
        return fields[TYPE_INDEX];
    }

    public String getAmountWithCurrency() {
        return fields[AMOUNT_INDEX];
    }

    public double getAmount() {
        double result = removeCurrency(fields[AMOUNT_INDEX].replace("Summa:",
                ""));
        if (getType().equals(" Popolnenie scheta"))
            return result;
        return -result;
    }

    public String getCurrency() {
        String amount = fields[AMOUNT_INDEX];
        int curStart = amount.indexOf('.') + 2;
        return amount.substring(curStart);
    }

    public double getAvailable() {
        return removeCurrency(fields[fields.length - 1]
                .replace("Dostupno:", ""));
    }

    public long getDate() {
        return DateHelper.parseSberbankDate(fields[fields.length - 2]);
    }

    private static double removeCurrency(String number) {
        int curStart = number.indexOf('.') + 2;
        return Double.valueOf(number.substring(0, curStart));
    }

    @Override
    public String toString() {
        return String.format("%s:%s=%.2f/%.2f", fields[fields.length - 2],
                fields[CARD_INDEX], getAmount(), getAvailable());
    }

}
