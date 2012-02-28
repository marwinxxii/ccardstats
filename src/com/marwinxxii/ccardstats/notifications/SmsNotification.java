package com.marwinxxii.ccardstats.notifications;

public interface SmsNotification {
    String getCard();

    double getAmount();

    // String getCurrency();
    long getDate();

    double getAvailable();
}
