package com.marwinxxii.ccardstats;

public interface NotificationService {
    String getAddress();

    SmsNotification parse(String body) throws IllegalArgumentException;
}
