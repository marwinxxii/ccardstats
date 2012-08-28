package com.github.marwinxxii.ccardstats.notifications;

public class SmsNotification {

	public String card;
	public double diff;
	public double balance;
	public int year;
	public int month;
	public int day;

	public SmsNotification(String card, double diff, double balance, int year,
			int month, int day) {
		this.card = card;
		this.diff = diff;
		this.balance = balance;
		this.year = year;
		this.month = month;
		this.day = day;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SmsNotification))
			return false;
		SmsNotification n = (SmsNotification) obj;
		return card.equals(n.card)
				&& diff == n.diff
				&& balance == n.balance
				&& day == n.day
				&& month == n.month
				&& year == n.year;
	}
}
