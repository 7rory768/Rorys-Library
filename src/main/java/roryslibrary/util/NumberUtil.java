package roryslibrary.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NumberUtil {
	
	private static String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
	
    private static final NavigableMap<Double, String> moneySuffixes = new TreeMap<>();
    private static final NavigableMap<String, Double> moneySuffixesReversed = new TreeMap<>();
	
	static {
		moneySuffixes.put(1_000d, "k");
		moneySuffixes.put(1_000_000d, "M");
		moneySuffixes.put(1_000_000_000d, "B");
		moneySuffixes.put(1_000_000_000_000d, "T");
		moneySuffixes.put(1_000_000_000_000_000d, "Q");
		moneySuffixes.put(1_000_000_000_000_000_000d, "P");
		moneySuffixes.put(1_000_000_000_000_000_000_000d, "E");
		moneySuffixes.put(1_000_000_000_000_000_000_000_000d, "Z");
        
        moneySuffixesReversed.put("k", 1_000d);
        moneySuffixesReversed.put("M", 1_000_000d);
        moneySuffixesReversed.put("B", 1_000_000_000d);
        moneySuffixesReversed.put("T", 1_000_000_000_000d);
        moneySuffixesReversed.put("Q", 1_000_000_000_000_000d);
        moneySuffixesReversed.put("P", 1_000_000_000_000_000_000d);
        moneySuffixesReversed.put("E", 1_000_000_000_000_000_000_000d);
        moneySuffixesReversed.put("Z", 1_000_000_000_000_000_000_000_000d);
	}
	
	public static String beautify(double value) {
		return beautify(value, 1);
	}
	
	public static String beautify(double value, int places) {
		//Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
		if (value == Double.MIN_VALUE) return beautify(Double.MIN_VALUE + 1, places);
		if (value < 0) return "-" + beautify(-value, places);
		if (value < 1000) return String.valueOf((int) value); //deal with easy case
		
		Map.Entry<Double, String> e = moneySuffixes.floorEntry(value);
		Double divideBy = e.getKey();
		String suffix = e.getValue();
		
		double truncated = value / (divideBy / 10); //the number part of the output times 10
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
		return hasDecimal ? NumberUtil.setMaxDecimals((truncated / 10d), places) + suffix : NumberUtil.setMaxDecimals((truncated / 10), places) + suffix;
	}
	
	public static String getCommaString(double number, int maxDecimals)
	{
		String commaString = setMaxDecimals(number, maxDecimals);
		int end = commaString.indexOf(".");
		if (end == -1) end = commaString.length();
		
		for (int i = end - 3; i > 0; i = i - 3)
		{
			commaString = commaString.substring(0, i) + "," + commaString.substring(i);
		}
		
		return commaString;
	}
	
	public static String getCommaString(double number)
	{
		return getCommaString(number, 2);
	}
	
	public static String getCommaString(long number)
	{
		String commaString = "" + number;
		for (int i = commaString.length() - 3; i > 0; i = i - 3)
		{
			commaString = commaString.substring(0, i) + "," + commaString.substring(i);
		}
		
		return commaString;
	}
	
	public static boolean isInt(String arg) {
		try {
			Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static boolean isPositiveInt(String arg) {
		try {
			int i = Integer.parseInt(arg);
			return i >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isNegativeInt(String arg) {
		try {
			int i = Integer.parseInt(arg);
			return i < 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isDouble(String arg) {
		try {
			Double.parseDouble(arg);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static boolean isPositiveDouble(String arg) {
		try {
			double i = Double.parseDouble(arg);
			return i >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isNegativeDouble(String arg) {
		try {
			double i = Double.parseDouble(arg);
			return i < 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isLong(String arg) {
		try {
			Long.parseLong(arg);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static boolean isPositiveLong(String arg) {
		try {
			double i = Long.parseLong(arg);
			return i >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isNegativeLong(String arg) {
		try {
			double i = Long.parseLong(arg);
			return i < 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static String formatMoney(String money) {
		int eIndex = money.indexOf("E");
		if (eIndex != -1) {
			int eValue = Integer.valueOf(money.substring(eIndex + 1, money.length()));
			money = money.substring(0, eIndex).replace(".", "");
			int bound = (eValue - money.length()) + 1;
			if (money.length() < eValue) {
				for (int i = 0; i < bound; i++) {
					money += "0";
				}
			}
		}
		String beforeDot = money;
		
		if (money.contains(".")) {
			beforeDot = money.substring(0, money.indexOf("."));
		}
		
		String newBeforeDot = beforeDot;
		for (int i = beforeDot.length() - 3; i > 0; i -= 3) {
			newBeforeDot = beforeDot.substring(0, i) + "," + newBeforeDot.substring(i, newBeforeDot.length());
		}
		return newBeforeDot;
	}
	
	public static String setMaxDecimals(double arg, int places)
	{
		if (places == 0)
		{
			String str = String.valueOf(arg);
			int eIndex = str.indexOf("E");
			
			if (eIndex != -1)
			{
				int eValue = Integer.valueOf(str.substring(eIndex + 1, str.length()));
				return str.substring(0, eValue + 1 + str.indexOf(".")).replace(".", "");
			}
			else
				return str.substring(0, str.indexOf("."));
		}
		
		long wholeArg = (long) arg;
		if (wholeArg == arg)
		{
			return String.valueOf(wholeArg);
		}
		
		StringBuilder suffix = new StringBuilder();
		for (int i = 0; i < places; i++)
		{
			suffix.append("#");
		}
		DecimalFormat decimalFormat = new DecimalFormat("0." + suffix);
		return decimalFormat.format(arg);
	}
	
	public static String ordinal(int i) {
		switch (i % 100) {
			case 11:
			case 12:
			case 13:
				return i + "th";
			default:
				return i + suffixes[i % 10];
			
		}
	}
	
	public static boolean isValidMoneyString(String arg)
	{
		arg = arg.toLowerCase();
		
		int index = 0;
		while (!arg.equals("") && index < arg.length())
		{
			
			char ch = arg.charAt(index);
			
			if (ch == '.')
			{
				if (index == arg.length() - 1) return false;
				ch = arg.charAt(++index);
			}
			
			if (ch == 'k' || ch == 'm' || ch == 'b' || ch == 't' || ch == 'q' || ch == 'p' || ch == 'e' || ch == 'z')
			{
				arg = arg.substring(index + 1);
				index = 0;
			} else if (!NumberUtil.isDouble("" + ch))
			{
				return false;
			} else
			{
				index++;
			}
			
		}
		
		return arg.length() == 0;
	}
	
	public static double parseValue(String arg)
	{
		arg = arg.toUpperCase();
		
		double value = 0;
		
		int index = 0;
		while (!arg.equals("") && index < arg.length())
		{
			char ch = arg.charAt(index);
			
			if (ch == '.')
			{
				if (index == arg.length() - 1) return -1;
				ch = arg.charAt(++index);
			}
			
			if (ch == 'K')
			{
				value += new BigDecimal(Double.parseDouble(arg.substring(0, index))).multiply(new BigDecimal(moneySuffixesReversed.get("k"))).doubleValue();
				arg = arg.substring(index + 1);
				index = 0;
			} else if (ch == 'M' || ch == 'B' || ch == 'T' || ch == 'Q' || ch == 'P' || ch == 'E' || ch == 'Z')
			{
				value += new BigDecimal(Double.parseDouble(arg.substring(0, index))).multiply(new BigDecimal(moneySuffixesReversed.get(String.valueOf(ch)))).doubleValue();
				arg = arg.substring(index + 1);
				index = 0;
			} else if (!NumberUtil.isInt("" + ch))
			{
				return -1;
			} else
			{
				index++;
			}
		}
		
		if (arg.length() > 0) return -1;
		
		return value;
	}
}
