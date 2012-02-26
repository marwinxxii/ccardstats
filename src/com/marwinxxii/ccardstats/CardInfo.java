package com.marwinxxii.ccardstats;

public class CardInfo {

    public Card card;
    public double monthIncome, monthOutcome;
    public double todayIncome, todayOutcome;

    public CardInfo(Card c, double monthIn, double monthOut,
            double todayIn, double todayOut) {
        this.card = c;
        this.monthIncome = monthIn;
        this.monthOutcome=monthOut;
        this.todayIncome = todayIn;
        this.todayOutcome = todayOut;
    }

    public void addMonthMoney(double diff) {
        if (diff < 0) {
            monthOutcome += -diff;
        } else {
            monthIncome += diff;
        }
    }

    public void addTodayMoney(double diff) {
        if (diff < 0) {
            todayOutcome += -diff;
        } else {
            todayIncome += diff;
        }
    }

    @Override
    public String toString() {
        return card + String.format("(month=(+%.2f;-%.2f);today=(+%.2f;-%.2f))",
                card, monthIncome, monthOutcome, todayIncome, todayOutcome);
    }
}
