package com.marwinxxii.ccardstats;

public class SberbankService implements NotificationService {

    private static final IllegalArgumentException EXCEPTION = new IllegalArgumentException();

    @Override
    public String getAddress() {
        return "900";
    }

    @Override
    public SmsNotification parse(String body) throws IllegalArgumentException {
        if (body.indexOf(';') == -1)
            throw EXCEPTION;
        return new SberbankNotification(body.split(";"));
    }

}
