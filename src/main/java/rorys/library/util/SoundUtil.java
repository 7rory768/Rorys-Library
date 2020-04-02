package rorys.library.util;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SoundUtil {
	
	public static void playSound(Player player, FileConfiguration config, String path) {
		if (!path.endsWith(".")) {
			path += ".";
		}

		Sound sound = null;
		String soundString = config.getString(path + "sound").toUpperCase();
		try {
			sound = Sound.valueOf(soundString);
		} catch (IllegalArgumentException e) {
			if (soundString.equals("BLOCK_NOTE_BLOCK_PLING")) {
				sound = Sound.valueOf("NOTE_PLING");
			} else if (soundString.equals("ENTITY_PLAYER_LEVELUP")) {
				sound = Sound.valueOf("LEVEL_UP");
			} else if (soundString.equals("BLOCK_ANVIL_PLACE")) {
				sound = Sound.valueOf("ANVIL_LAND");
			}
		}
		player.playSound(player.getLocation(), sound, Float.valueOf(config.getString(path + "volume", "1.0")), Float.valueOf(config.getString(path + "pitch", "1.0")));
	}
	
}
