package roryslibrary.potions;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/*
 * Developed by Rory Skipper (Roree) on 2021-09-02
 */
@Getter
public class PotionInfo {
	
	private final PotionEffectType type;
	private final int amplifier;
	private final int duration;
	
	private PotionEffect potionEffect;
	
	public PotionInfo(ConfigurationSection section) {
		PotionEffectType type;
		
		try {
			type = PotionEffectType.getByName(section.getString("effect"));
		} catch (IllegalArgumentException e) {
			type = null;
			Bukkit.getLogger().severe("[PotionUtil] Failed to load potion type `" + section.getString("effect") + "` @ " + section.getCurrentPath());
		}
		
		this.type = type;
		
		this.amplifier = section.getInt("amplifier");
		this.duration = section.getInt("duration") * 20;
		
		if (type != null)
			this.potionEffect = new PotionEffect(this.type, this.duration, this.amplifier);
	}
	
	public PotionInfo(PotionEffectType type, int amplifier, int duration) {
		this.type = type;
		this.amplifier = amplifier;
		this.duration = duration <= -1 ? Integer.MAX_VALUE : duration;
		
		if (type != null)
			this.potionEffect = new PotionEffect(this.type, this.duration, this.amplifier);
	}
	
	public void apply(LivingEntity entity) {
		if (potionEffect == null) return;
		
		PotionEffect current = null;
		
		for (PotionEffect effect : entity.getActivePotionEffects()) {
			if (effect.getType() == type) {
				current = effect;
				break;
			}
		}
		
		if (current != null) {
			if (current.getAmplifier() > amplifier || (current.getAmplifier() == this.amplifier && current.getDuration() > duration))
				return;
			
			entity.removePotionEffect(type);
		}
		
		entity.addPotionEffect(potionEffect);
	}
	
	public void remove(LivingEntity entity) {
		if (potionEffect == null) return;
		
		entity.removePotionEffect(type);
	}
}
