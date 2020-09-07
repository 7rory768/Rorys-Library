package roryslibrary.sounds;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundInfo {
	
	private final Sound sound;
	private final float volume, pitch;
	
	public SoundInfo(Sound sound, float volume, float pitch) {
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	public void play(Player p) {
		play(p, p.getLocation());
	}
	
	public void play(Player p, Location loc) {
		p.playSound(loc, sound, volume, pitch);
	}
}
