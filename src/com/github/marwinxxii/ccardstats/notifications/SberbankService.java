package com.github.marwinxxii.ccardstats.notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.marwinxxii.ccardstats.helpers.DateHelper;
import com.github.marwinxxii.ccardstats.helpers.MoneyHelper;

public class SberbankService implements NotificationService {
    
    private static interface ServiceImplementation {
        SmsNotification recognise(String body) throws IllegalArgumentException;
    }

    private static SimpleDateFormat sberbankDateFormat = new SimpleDateFormat(
            "dd.MM.yy HH:mm"),
            sberbankDateWithoutTimeFormat = new SimpleDateFormat("dd.MM.yy");

    private static final IllegalArgumentException EXCEPTION = new IllegalArgumentException();
    
    // newest first, oldest last
    private static final ServiceImplementation[] implementations = {
        new Version3(), new Version2RU(), new Version2(), new Version1()
    };

    @Override
    public String getAddress() {
        return "900";
    }
    
    @Override
    public SmsNotification recognise(String body) throws IllegalArgumentException {
        for(ServiceImplementation impl:implementations) {
            try {
                return impl.recognise(body);
            } catch(IllegalArgumentException e) {
            }
        }
        throw EXCEPTION;
    }
    
    public static class Version1 implements ServiceImplementation {
        
        private static final int INDEX_CARD = 0;
        private static final int INDEX_OPERATION = 1;
        private static final int INDEX_AMOUNT = 3;
        
        @Override
		public SmsNotification recognise(String body)
				throws IllegalArgumentException {
			if (body.indexOf(';') == -1)
				throw EXCEPTION;
			String[] fields = body.toLowerCase().split(";");
			Date date = parseDate(fields[fields.length - 2]);
			double balance = MoneyHelper
					.parseCurrency(fields[fields.length - 1].replace(
							"dostupno:", ""));
			double diff = MoneyHelper.parseCurrency(fields[INDEX_AMOUNT]
					.replace("summa:", ""));
			String operation = fields[INDEX_OPERATION].trim();
			if (!operation.equals("popolnenie scheta")
				&& !operation.equals("beznalichny perevod sredstv")
				&& !operation.contains("otmena")) {
				diff = -diff;
			}
			return new SmsNotification(fields[INDEX_CARD].toUpperCase(), diff,
					balance, date.getYear() + 1900, date.getMonth() + 1,
					date.getDate());
		}
        
        public static Date parseDate(String date) {
            if (date.indexOf("msk") != -1) {
                date = date.replace("msk", "");
            }
            date = date.trim();
            try {
                return sberbankDateFormat.parse(date);
            } catch (ParseException e) {
                return DateHelper.Today;
            }
        }
    }
    
    public static class Version2 implements ServiceImplementation {
        
        private static final int INDEX_SUM = 1;
        private static final int INDEX_SUM_CURRENCY = 2;
        private static final int INDEX_CARD = 4;
        private static final int INDEX_DATE = 5;
        //private static final int INDEX_TIMEZONE = 6;
        private static final int INDEX_BALANCE = 7;
        private static final int INDEX_BALANCE_CUR = 8;
        
        private static final Pattern PATTERN = Pattern.compile(
                //(sum) (currency) (card) (date)(timezone)? (balance) (currency)
        		".+ na summu (-?\\d{1,9}\\.\\d{2})\\s?(\\w+)?\\.(.+)? po karte (\\w+) .+\\. (\\d{2}\\.\\d{2}\\.\\d{2,4} \\d{2}:\\d{2})\\s?(\\w+)?\\. dostupno: (\\d{1,9}\\.\\d{2})\\s?(\\w+)?\\.$",
                Pattern.CASE_INSENSITIVE);
        
        @Override
        public SmsNotification recognise(String body) throws IllegalArgumentException {
            Matcher m = PATTERN.matcher(body);
            if (!m.matches() || body.contains("ne vypolnena"))
                throw EXCEPTION;
            Date date;
            try {
                date = sberbankDateFormat.parse(m.group(INDEX_DATE));
            } catch (ParseException e) {
                date = DateHelper.Today;
            }
            double balance = MoneyHelper.parseCurrency(m.group(INDEX_BALANCE),
                    m.group(INDEX_BALANCE_CUR));
            double diff = MoneyHelper.parseCurrency(m.group(INDEX_SUM),
                    m.group(INDEX_SUM_CURRENCY));
            if (!body.startsWith("Operaciya zachisleniya")) {
                diff = -diff;
            }
			int year = date.getYear() + 1900;
			if (year < 2012)
				year += 100;
			return new SmsNotification(m.group(INDEX_CARD), diff, balance,
					year, date.getMonth() + 1, date.getDate());
        }
    }

    public static class Version2RU implements ServiceImplementation {
        
        private static final int INDEX_SUM = 1;
        private static final int INDEX_SUM_CURRENCY = 2;
        private static final int INDEX_CARD = 3;
        private static final int INDEX_DATE = 4;
        //private static final int INDEX_TIMEZONE = 5;
        private static final int INDEX_BALANCE = 5;
        private static final int INDEX_BALANCE_CUR = 6;
        
        private static final Pattern PATTERN = Pattern.compile(
        		//.+ (\d{1,9}\.\d{2})\s?(.{2,10}) .+ (\w{4,8}\d{4}) .+ (\d{2}\.\d{2}\.\d{2} \d{2}:\d{2}).+ (\d{1,9}\.\d{2})\s?(.+)$
        	 	".+ "
        		+"(-?\\d{1,9}\\.\\d{2})" //sum
        	 	+"\\s?([^\\s]{2,10})" //currency
        		+" .+ "
        	 	+"(\\w{4,8}\\d{4})" //card
        		+" .+ "
        	 	+"(\\d{2}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2})" //date
        		+".+ "
        	 	+"(\\d{1,9}\\.\\d{2})" //balance
        		+"\\s?(.+)$" //currency
        		,
                Pattern.CASE_INSENSITIVE);
        
        @Override
        public SmsNotification recognise(String body) throws IllegalArgumentException {
            Matcher m = PATTERN.matcher(body);
            if (!m.matches() || body.contains("vypolnena") || body.contains("не выполнена"))
                throw EXCEPTION;
            Date date;
            try {
                date = sberbankDateFormat.parse(m.group(INDEX_DATE));
            } catch (ParseException e) {
                date = DateHelper.Today;
            }
            double balance = MoneyHelper.parseCurrency(m.group(INDEX_BALANCE),
                    m.group(INDEX_BALANCE_CUR));
            double diff = MoneyHelper.parseCurrency(m.group(INDEX_SUM),
                    m.group(INDEX_SUM_CURRENCY));
            if (!body.startsWith("Операция зачисления")) {
                diff = -diff;
            }
            int year = date.getYear() + 1900;
            if (year < 2012) year += 100;
            return new SmsNotification(m.group(INDEX_CARD), diff, balance, year,
                    date.getMonth() + 1, date.getDate());
        }
    }
    
    public static class Version3 implements ServiceImplementation {
    	
    	private static final Pattern PATTERN = Pattern.compile(
    			"([\\w\\d]+): " //card
    			+"(\\d{2}\\.\\d{2}\\.\\d{2}( \\d{2}:\\d{2})?)(.{2,5})?\\.?" //date
    			+" (.+) " //operation
    			+"(-?\\d{1,9}\\.\\d{2})" //sum
        	 	+"\\s?([^\\s]{2,10})" //currency
        	 	+" .+ "
        	 	+"(\\d{1,9}\\.\\d{2})" //balance
        	 	+"\\s?(.{2,10})$" //currency
    			,
    			Pattern.CASE_INSENSITIVE);
    	
    	private static final int INDEX_CARD = 1,
    			INDEX_DATE = 2,
    			INDEX_OPERATION = 5,
    			INDEX_SUM = 6,
    			INDEX_SUM_CURRENCY = 7,
    			INDEX_BALANCE = 8,
    			INDEX_BALANCE_CURRENCY = 9;
    	
    	@Override
		public SmsNotification recognise(String body)
				throws IllegalArgumentException {
			Matcher m = PATTERN.matcher(body);
			if (!m.matches())
				throw EXCEPTION;
			Date date;
			try {
				date = sberbankDateFormat.parse(m.group(INDEX_DATE));
			} catch (ParseException e) {
				try {
					date = sberbankDateWithoutTimeFormat.parse(m.group(INDEX_DATE));
				} catch (ParseException e2) {
					date = DateHelper.Today;
				}
			}
			double balance = MoneyHelper.parseCurrency(m.group(INDEX_BALANCE),
					m.group(INDEX_BALANCE_CURRENCY));
			double diff = MoneyHelper.parseCurrency(m.group(INDEX_SUM),
					m.group(INDEX_SUM_CURRENCY));
			String operation = m.group(INDEX_OPERATION).trim();
			if (!operation.startsWith("операция зачисления")) {
				diff = -diff;
			}
			int year = date.getYear() + 1900;
			if (year < 2012)
				year += 100;
			
			return new SmsNotification(m.group(INDEX_CARD), diff, balance,
					year, date.getMonth() + 1, date.getDate());
		}
    }
}
