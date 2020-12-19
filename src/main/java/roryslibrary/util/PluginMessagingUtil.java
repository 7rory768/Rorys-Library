package roryslibrary.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Rory on 7/08/2020.
 */
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
