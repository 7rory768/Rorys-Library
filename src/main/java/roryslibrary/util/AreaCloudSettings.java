package roryslibrary.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AreaEffectCloud;

/*
 * Developed by Rory Skipper (Roree) on 2022-09-24
 */
@Getter
@Setter
public class AreaCloudSettings {
	private int duration;
	private int durationOnUse;
	private int reapplicationDelay;
	private int waitTime;
	private float radius;
	private float radiusOnUse;
	private float radiusPerTick;
	private Color cloudColor;
	
	public AreaCloudSettings(FileConfiguration config) {
		duration = config.getInt("duration", 600);
		durationOnUse = config.getInt("duration-on-use", 100);
		reapplicationDelay = config.getInt("reapplication-delay", 0);
		waitTime = config.getInt("wait-time");
		radius = (float) config.getDouble("radius");
		radiusOnUse =  (float) config.getDouble("radius-on-use");
		radiusPerTick =  (float) config.getDouble("radius-per-tick");
		cloudColor = Color.fromRGB(config.getInt("color.r", 0), config.getInt("color.g", 0), config.getInt("color.b", 0));
	}
	
	public void apply(AreaEffectCloud cloud) {
		cloud.setDuration(duration);
		cloud.setDurationOnUse(durationOnUse);
		cloud.setReapplicationDelay(reapplicationDelay);
		cloud.setWaitTime(waitTime);
		cloud.setRadius(radius);
		cloud.setRadiusOnUse(radiusOnUse);
		cloud.setRadiusPerTick(radiusPerTick);
		cloud.setColor(cloudColor);
	}
}
