package com.github.marwinxxii.ccardstats.notifications;

public abstract class SmsParser {

    public static SmsNotification parse(String body) {
        for (NotificationService ns : NotificationService.SERVICES) {
            try {
                return ns.recognise(body);
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        return null;
    }

    public static SmsNotification parse(String address, String body) {
        for (NotificationService ns : NotificationService.SERVICES) {
            if (!ns.getAddress().equals(address))
                continue;
            try {
                return ns.recognise(body);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                continue;
            }
        }
        return null;
    }
}
