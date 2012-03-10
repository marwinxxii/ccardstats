package com.marwinxxii.ccardstats.helpers;


public class MoneyHelper {
    
    public static String formatMoney(double value, boolean isIncome) {
        String format;
        if (value < 0.01) {
            format = "%.2f";
        } else {
            format = isIncome ?"+%.2f":"-%.2f";
        }
        return String.format(format, value);
    }
}
