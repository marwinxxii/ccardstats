package com.github.marwinxxii.ccardstats.db;

public class Card {

    private String name;
    private String alias;
    private double balance;

    public Card(String name, String alias, double balance) {
        this.name = name;
        this.alias = alias;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
