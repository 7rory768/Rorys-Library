package roryslibrary.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMessagingUtil extends MessagingUtil {
	
	private final JavaPlugin plugin;
	
	public PluginMessagingUtil(JavaPlugin plugin) {
		this.plugin = plugin;
		reload();
	}
	
	@Override
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}
	
}
