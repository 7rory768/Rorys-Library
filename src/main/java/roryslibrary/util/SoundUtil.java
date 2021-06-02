package roryslibrary.util;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import roryslibrary.sounds.SoundInfo;

public class SoundUtil {
	
	@Deprecated
	public static void playSound(Player player, JavaPlugin plugin, String path) {
		playSound(player, plugin.getConfig(), path);
	}
	
	@Deprecated
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
	
	public static SoundInfo getSoundInfo(JavaPlugin plugin, String path) {
		return getSoundInfo(plugin.getConfig(), path);
	}
	
	public static SoundInfo getSoundInfo(FileConfiguration config, String path) {
		if (path.endsWith(".")) path = path.substring(0, path.length() - 1);
		return getSoundInfo(config.getConfigurationSection(path));
	}
	
	public static SoundInfo getSoundInfo(ConfigurationSection section) {
		Sound sound = null;
		String soundString = section.getString("sound").toUpperCase();
		try {
			sound = Sound.valueOf(soundString);
		} catch (IllegalArgumentException e) {
			if (soundString.equals("BLOCK_NOTE_BLOCK_PLING")) sound = Sound.valueOf("NOTE_PLING");
			else if (soundString.equals("ENTITY_PLAYER_LEVELUP")) sound = Sound.valueOf("LEVEL_UP");
			else if (soundString.equals("BLOCK_ANVIL_PLACE")) sound = Sound.valueOf("ANVIL_LAND");
			else
				Bukkit.getLogger().severe("[SoundUtil] Failed to load sound `" + soundString + "` @ " + section.getCurrentPath());
		}
		
		if (sound != null) {
			float volume = Float.valueOf(section.getString("volume", "1.0"));
			float pitch = Float.valueOf(section.getString("pitch", "1.0"));
			return new SoundInfo(sound, volume, pitch);
		}
		
		return null;
	}
	
}
