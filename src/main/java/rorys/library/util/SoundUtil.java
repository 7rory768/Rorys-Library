package rorys.library.util;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SoundUtil {
	
	public static void playSound(Player player, FileConfiguration config, String path) {
		if (!path.endsWith(".")) {
			path += ".";
		}
		
		try {
			player.playSound(player.getLocation(), Sound.valueOf(config.getString(path + "sound")), Float.valueOf(config.getString(path + "volume", "1.0")), Float.valueOf(config.getString(path + "pitch", "0.5")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
