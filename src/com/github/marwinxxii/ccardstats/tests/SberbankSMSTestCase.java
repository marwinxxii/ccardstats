package com.github.marwinxxii.ccardstats.tests;

import java.util.HashMap;

import com.github.marwinxxii.ccardstats.helpers.MoneyHelper;
import com.github.marwinxxii.ccardstats.notifications.SberbankService;
import com.github.marwinxxii.ccardstats.notifications.SmsNotification;

import junit.framework.TestCase;

public class SberbankSMSTestCase extends TestCase {

	static final HashMap<String, Double> rates = new HashMap<String, Double>();
	static final String card = "VISA1111";
	static final double diff = 10.0, balance = 10.0;
	static final int year = 2012, month = 10, day = 1;
	static final SmsNotification expectedPositive = new SmsNotification(card,
			diff, balance, year, month, day),
			expectedNegative = new SmsNotification(card, -diff, balance, year,
					month, day);

	static {
		rates.put("rur", 1.0);
		rates.put("eur", 40.0);
		rates.put("ron", 9.0);
		rates.put("huf", 0.14);
	}

	@Override
	public void setUp() {
		MoneyHelper.setExchangeRates(rates);
	}

	public void testVersion1() {
		String messageBody = String
				.format("%s; Beznalichny perevod sredstv; Uspeshno; Summa:%.2fRUR; ITT 89898 0002; %02d.%02d.%04d 19:13; Dostupno:%.2f",
						card, diff, day, month, year, balance);
		assertEquals(expectedPositive,
				new SberbankService.Version1().recognise(messageBody));
	}

	public void testVersion1_2() {
		String messageBody = String
				.format("%s; Popolnenie scheta; Uspeshno; Summa:%.2fRUR; %02d.%02d.%04d 19:13; Dostupno:%.2f",
						card, diff, day, month, year, balance);
		assertEquals(expectedPositive,
				new SberbankService.Version1().recognise(messageBody));
	}

	public void testVersion1_3() {
		String messageBody = String
				.format("%s; Pokupka; Uspeshno; Summa:%.2fRUR; N11, O'KEY; %02d.%02d.%04d 19:13; Dostupno:%.2fRUR",
						card, diff, day, month, year, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version1().recognise(messageBody));
	}

	public void testVersion1_4() {
		String messageBody = String
				.format("%s; Vydacha nalichnyh; Uspeshno; Summa:%.2fRUR; BANKOMAT 11 8074; %02d.%02d.%04d 19:13; Dostupno:%.2fRUR",
						card, diff, day, month, year, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version1().recognise(messageBody));
	}

	public void testVersion1_5() {
		String messageBody = String
				.format("%s; Oplata uslug mobilnogo banka za period s 20/12/2011 po 19/012012; Uspeshno; Summa:%.2fRUR; N11, O'KEY; %02d.%02d.%04d 19:13MSK; Dostupno:%.2fRUR",
						card, diff, day, month, year, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version1().recognise(messageBody));
	}

	public void testVersion1_6() {
		String messageBody = String
				.format("%s; Spisanie: perevod sredstv na karty; Uspeshno; Summa:%.2fRUR; SBOL; %02d.%02d.%04d 19:13; Dostupno:%.2fRUR",
						card, diff, day, month, year, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version1().recognise(messageBody));
	}

	public void testVersion2() {
		String messageBody = String
				.format("Oplata uslugi Mobil'ny bank za period 20/05/2012 - 19/06/2012 na summu %.2f rub. po karte %s vypolnena uspeshno. %02d.%02d.%04d 22:12MSK. Dostupno: %.2f rub.",
						diff, card, day, month, year, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version2().recognise(messageBody));
	}

	public void testVersion2_2() {
		SberbankService.Version2 service = new SberbankService.Version2();
		String messageBody = String
				.format("Operaciya zachisleniya na summu %.2f rub. po karte %s vypolnena uspeshno. %02d.%02d.%04d 22:12MSK. Dostupno: %.2f rub.",
						diff, card, day, month, year, balance);
		assertEquals(expectedPositive, service.recognise(messageBody));

		// year as 2 digits
		messageBody = String
				.format("Operaciya zachisleniya na summu %.2f rub. po karte %s vypolnena uspeshno. %02d.%02d.%02d 12:41. Dostupno: %.2f rub.",
						diff, card, day, month, 12, balance);
		assertEquals(expectedPositive, service.recognise(messageBody));
	}

	public void testVersion2_3() {
		String messageBody = String
				.format("Vydacha nalichnyh na summu %.2f rub. BANKOMAT 201459 8074 po karte %s vypolnena uspeshno. %02d.%02d.%04d 22:12MSK. Dostupno: %.2f rub.",
						diff, card, day, month, year, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version2().recognise(messageBody));
	}

	public void testVersion2Ru() {
		String messageBody = String
				.format("Выдача наличных на сумму %.2f руб. BANKOMAT 201459 80740 по карте %s выполнена успешно. %02d.%02d.%02d 19:27. Доступно: %.2f руб.",
						diff, card, day, month, 12, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version2RU().recognise(messageBody));
	}

	public void testVersion2Ru_1() {
		String messageBody = String
				.format("Покупка на сумму %.2f руб. PP *CL по карте %s выполнена успешно. %02d.%02d.%02d 19:27. Доступно: %.2f руб.",
						diff, card, day, month, 12, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version2RU().recognise(messageBody));
	}

	public void testVersion2Ru_2() {
		String messageBody = String
				.format("Оплата услуг на сумму %.2f руб. SBERBANK ONL@IN PLATEZH по карте %s выполнена успешно. %02d.%02d.%02d 19:27. Доступно: %.2f руб.",
						diff, card, day, month, 12, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version2RU().recognise(messageBody));
	}

	public void testVersion2Ru_3() {
		String messageBody = String
				.format("Операция зачисления на сумму %.2f руб. по карте %s выполнена успешно. %02d.%02d.%02d 19:27. Доступно: %.2f руб.",
						diff, card, day, month, 12, balance);
		assertEquals(expectedPositive,
				new SberbankService.Version2RU().recognise(messageBody));
	}

	public void testVersion3() {
		String messageBody = String
				.format("%s: %02d.%02d.%02d 19:27 покупка на сумму %.2f руб. anywhere выполнена успешно. Доступно: %.2f руб.",
						card, day, month, 12, diff, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version3().recognise(messageBody));
	}

	public void testVersion3_2() {
		String messageBody = String
				.format("%s: %02d.%02d.%02d 19:27МСК оплата услуги Мобильный банк за период 01/01/2011 - 01/01/2012 на сумму %.2f руб. anywhere выполнена успешно. Доступно: %.2f руб.",
						card, day, month, 12, diff, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version3().recognise(messageBody));
	}

	public void testVersion3_3() {
		String messageBody = String
				.format("%s: %02d.%02d.%02d 19:27 выдача наличных на сумму %.2f руб. BANKOMAT 9988 9988 выполнена успешно. Доступно: %.2f руб.",
						card, day, month, 12, diff, balance);
		assertEquals(expectedNegative,
				new SberbankService.Version3().recognise(messageBody));
	}

	public void testVersion3_4() {
		SberbankService.Version3 service = new SberbankService.Version3();

		String messageBody = String
				.format("%s: %02d.%02d.%02d оплата услуги Мобильный банк на сумму %.2f руб. выполнена успешно. Доступно: %.2f руб.",
						card, day, month, 12, diff, balance);
		assertEquals(expectedNegative, service.recognise(messageBody));

		messageBody = String
				.format("%s: %02d.%02d.%02d отмена платы за услугу Мобильный банк на сумму %.2f руб. выполнена успешно. Доступно: %.2f руб.",
						card, day, month, 12, -diff, balance);
		assertEquals(expectedPositive, service.recognise(messageBody));
	}

	public void testVersion3_5() {
		String messageBody = String
				.format("%s: %02d.%02d.%02d 19:27 операция зачисления на сумму %.2f руб. выполнена успешно. Доступно: %.2f руб.",
						card, day, month, 12, diff, balance);

		SberbankService.Version3 service = new SberbankService.Version3();

		assertEquals(expectedPositive, service.recognise(messageBody));

		messageBody = String
				.format("%s: %02d.%02d.%02d 19:27 операция зачисления на сумму %.2fRON выполнена успешно. Доступно: %.2f руб.",
						card, day, month, 12, diff, balance);
		assertEquals(new SmsNotification(card, diff * rates.get("ron"),
				balance, year, month, day), service.recognise(messageBody));

		messageBody = String
				.format("%s: %02d.%02d.%02d 19:27 операция зачисления на сумму %.2fHUF выполнена успешно. Доступно: %.2f руб.",
						card, day, month, 12, diff, balance);
		assertEquals(new SmsNotification(card, diff * rates.get("huf"),
				balance, year, month, day), service.recognise(messageBody));

		messageBody = String
				.format("%s: %02d.%02d.%02d 19:27 покупка на сумму %.2fEUR NORD 5 выполнена успешно. Доступно: %.2f руб.",
						card, day, month, 12, diff, balance);
		assertEquals(new SmsNotification(card, -diff * rates.get("eur"), balance,
				year, month, day), service.recognise(messageBody));
	}
}
