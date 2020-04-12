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

	public String getNiceName(Enchantment enchantment)
	{
		String name = enchantment.getName();
		switch (name) {
			case "ARROW_DAMAGE": return "Power";
			case "ARROW_FIRE": return "Flame";
			case "ARROW_INFINITE": return "Infinity";
			case "ARROW_KNOCKBACK": return "Punch";
			case "BINDING_CURSE": return "§cCurse of Binding";
			case "CHANNELING": return "Chanelling";
			case "DAMAGE_ALL": return "Sharpness";
			case "DAMAGE_ARTHROPODS": return "Bane of Arthropods";
			case "DAMAGE_UNDEAD": return "Smite";
			case "DEPTH_STRIDER": return "Depth Strider";
			case "DIG_SPEED": return "Efficiency";
			case "DURABILITY": return "Durability";
			case "FIRE_ASPECT": return "Fire Aspect";
			case "FROST_WALKER": return "Frost Walker";
			case "IMPALING": return "Impaling";
			case "KNOCKBACK": return "Knockback";
			case "LOOT_BONUS_BLOCKS": return "Fortune";
			case "LOOT_BONUS_MOBS": return "Looting";
			case "LOYALTY": return "Loyalty";
			case "LUCK": return "Luck";
			case "LURE": return "Lure";
			case "MENDING": return "Mending";
			case "MULTISHOT": return "Multishot";
			case "OXYGEN": return "Respiration";
			case "PIERCING": return "Piercing";
			case "PROTECTION_ENVIRONMENTAL": return "Protection";
			case "PROTECTION_EXPLOSIONS": return "Blast Protection";
			case "PROTECTION_FALL": return "Feather Falling";
			case "PROTECTION_PROJECTILE": return "Projectile Protection";
			case "QUICK_CHARGE": return "Quick Charge";
			case "RIPTIDE": return "Riptide";
			case "SILK_TOUCH": return "Silk Touch";
			case "SWEEPING_EDGE": return "Sweeping_Edge";
			case "THORNS": return "Thorns";
			case "VANISHING_CURSE": return "§cCurse of Vanishing";
			case "WATER_WORKER": return "Water Worker";
		}

		return "Unknown Enchant";
	}


	static String intToRoman(int num) {
		try {
			return m[num / 1000] + c[(num % 1000) / 100] + x[(num % 100) / 10] + i[num % 10];
		} catch (ArrayIndexOutOfBoundsException e) {
			return "" + num;
		}
	}

}
