package roryslibrary.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import roryslibrary.configs.CustomConfig;

public class ConfigMessagingUtil extends MessagingUtil {
	
	private final CustomConfig customConfig;
	
	public ConfigMessagingUtil(CustomConfig config) {
		this.customConfig = config;
		reload();
	}
	
	@Override
	public FileConfiguration getConfig() {
		return customConfig.getConfig();
	}
	
}
