package com.marwinxxii.ccardstats.notifications;

public interface NotificationService {
    String getAddress();

    SmsNotification parse(String body) throws IllegalArgumentException;
}
