package rorys.library.util;

import org.bukkit.enchantments.Enchantment;

public class EnchantUtil {

	private static String m[] = {"", "M", "MM", "MMM"};
	private static String c[] = {"", "C", "CC", "CCC", "CD", "D",
	                             "DC", "DCC", "DCCC", "CM"};
	private static String x[] = {"", "X", "XX", "XXX", "XL", "L",
	                             "LX", "LXX", "LXXX", "XC"};
	private static String i[] = {"", "I", "II", "III", "IV", "V",
	                             "VI", "VII", "VIII", "IX"};

	public static String getNiceName(Enchantment enchantment) {
		String name = "";

		for (String arg : enchantment.getKey().toString().substring(10).split("_")) {
			name += arg.substring(0, 1).toUpperCase() + arg.substring(1) + " ";
		}

		return name.trim();
	}

	static String intToRoman(int num) {
		try {
			return m[num / 1000] + c[(num % 1000) / 100] + x[(num % 100) / 10] + i[num % 10];
		} catch (ArrayIndexOutOfBoundsException e) {
			return "" + num;
		}
	}

}
