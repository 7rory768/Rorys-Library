package roryslibrary.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import roryslibrary.potions.PotionInfo;

/*
 * Developed by Rory Skipper (Roree) on 2021-09-02
 */
public class PotionUtil {
	
	public static void applyPotion(Player player, JavaPlugin plugin, String path) {
		applyPotion(player, plugin.getConfig(), path);
	}
	
	public static void applyPotion(Player player, FileConfiguration config, String path) {
		if (path.endsWith(".")) path = path.substring(0, path.length() - 1);
		
		applyPotion(player, config.getConfigurationSection(path));
	}
	
	public static void applyPotion(Player player, ConfigurationSection section) {
		PotionInfo potionInfo = getPotionInfo(section);
		if (potionInfo != null) potionInfo.apply(player);
	}
	
	public static void removePotion(Player player, JavaPlugin plugin, String path) {
		removePotion(player, plugin.getConfig(), path);
	}
	
	public static void removePotion(Player player, FileConfiguration config, String path) {
		if (path.endsWith(".")) path = path.substring(0, path.length() - 1);
		
		removePotion(player, config.getConfigurationSection(path));
	}
	
	public static void removePotion(Player player, ConfigurationSection section) {
		PotionInfo potionInfo = getPotionInfo(section);
		if (potionInfo != null) potionInfo.remove(player);
	}
	
	public static PotionInfo getPotionInfo(JavaPlugin plugin, String path) {
		return getPotionInfo(plugin.getConfig(), path);
	}
	
	public static PotionInfo getPotionInfo(FileConfiguration config, String path) {
		if (path.endsWith(".")) path = path.substring(0, path.length() - 1);
		return getPotionInfo(config.getConfigurationSection(path));
	}
	
	public static PotionInfo getPotionInfo(ConfigurationSection section) {
		if (section == null) return null;
		
		return new PotionInfo(section);
	}
	
}
