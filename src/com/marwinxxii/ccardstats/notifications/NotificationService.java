package com.marwinxxii.ccardstats.notifications;

public interface NotificationService {

    public static final NotificationService[] SERVICES = { new SberbankService() };

    public String getAddress();

    public SmsNotification recognise(String body)
            throws IllegalArgumentException;
}
