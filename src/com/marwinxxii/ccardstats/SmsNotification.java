package com.marwinxxii.ccardstats;

public interface SmsNotification {
    String getCard();
    double getAmount();
    //String getCurrency();
    long getDate();
    double getAvailable();
}
