package roryslibrary.util;

import org.bukkit.configuration.file.FileConfiguration;
import roryslibrary.configs.CustomConfig;

/**
 * Created by Rory on 7/08/2020.
 */
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
