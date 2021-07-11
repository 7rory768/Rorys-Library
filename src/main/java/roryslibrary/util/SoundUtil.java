package roryslibrary.util;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import roryslibrary.sounds.SoundInfo;

public class SoundUtil
{
	
	public static void playSound(Player player, JavaPlugin plugin, String path)
	{
		playSound(player, plugin.getConfig(), path);
	}
	
	public static void playSound(Player player, FileConfiguration config, String path)
	{
		if (path.endsWith(".")) path = path.substring(0, path.length() - 1);
		
		playSound(player, config.getConfigurationSection(path));
	}
	
	public static void playSound(Player player, ConfigurationSection section)
	{
		SoundInfo soundInfo = getSoundInfo(section);
		if (soundInfo != null) soundInfo.play(player);
	}
	
	public static SoundInfo getSoundInfo(JavaPlugin plugin, String path)
	{
		return getSoundInfo(plugin.getConfig(), path);
	}
	
	public static SoundInfo getSoundInfo(FileConfiguration config, String path)
	{
		if (path.endsWith(".")) path = path.substring(0, path.length() - 1);
		return getSoundInfo(config.getConfigurationSection(path));
	}
	
	public static SoundInfo getSoundInfo(ConfigurationSection section)
	{
		if (section == null) return null;
		
		Sound  sound;
		String soundString = section.getString("sound", "NULL").toUpperCase();
		
		try
		{
			sound = Sound.valueOf(soundString);
		} catch (IllegalArgumentException e)
		{
			if (soundString.equals("BLOCK_NOTE_BLOCK_PLING")) sound = Sound.valueOf("NOTE_PLING");
			else if (soundString.equals("ENTITY_PLAYER_LEVELUP")) sound = Sound.valueOf("LEVEL_UP");
			else if (soundString.equals("BLOCK_ANVIL_PLACE")) sound = Sound.valueOf("ANVIL_LAND");
			else
				Bukkit.getLogger().severe("[SoundUtil] Failed to load sound `" + soundString + "` @ " + section.getCurrentPath());
			
			return null;
		}
		
		float volume = Float.parseFloat(section.getString("volume", "1.0"));
		float pitch  = Float.parseFloat(section.getString("pitch", "1.0"));
		return new SoundInfo(sound, volume, pitch);
	}
	
}
