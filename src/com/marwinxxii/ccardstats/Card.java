package com.marwinxxii.ccardstats;

public class Card {
    
    protected String name;
    protected String alias;
    public double income = 0;
    public double outcome = 0;
    public double available = 0;

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

    public double getIncome() {
        return income;
    }
    
    public String getIncomeStr() {
        return String.format("%.2f", income);
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getOutcome() {
        return outcome;
    }
    
    public String getOutcomeStr() {
        return String.format("%.2f", outcome);
    }

    public void setOutcome(double outcome) {
        this.outcome = outcome;
    }

    public double getAvailable() {
        return available;
    }
    
    public String getAvailableStr() {
        return String.format("%.2f", available);
    }

    public void setAvailable(double available) {
        this.available = available;
    }

    public Card(String name, String alias, double available,
            double income, double outcome) {
        this.name = name;
        this.alias = alias;
        this.available = available;
        this.income = income;
        this.outcome= outcome;
    }
}
