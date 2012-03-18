package com.github.marwinxxii.ccardstats.helpers;

import java.util.Map;


public class MoneyHelper {
    
    private static Map<String, Double> exchangeRates;
    
    public static void setExchangeRates(Map<String, Double> rates) {
        exchangeRates = rates;
    }
    
    public static String formatMoney(double value, boolean isIncome) {
        String format;
        if (value < 0.01) {
            format = "%.2f";
        } else {
            format = isIncome ?"+%.2f":"-%.2f";
        }
        return String.format(format, value);
    }
    
    public static double parseCurrency(String number) {
        for (String cur:exchangeRates.keySet()) {
            if (number.indexOf(cur) != -1) {
                number = number.replace(cur, "").trim();
                return Double.parseDouble(number) * exchangeRates.get(cur);
            }
        }
        return Double.parseDouble(number.replace("rur", ""));
    }
}
