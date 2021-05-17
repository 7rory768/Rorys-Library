package roryslibrary.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Rory on 6/22/2017.
 */
public abstract class MessagingUtil {
	
	@Setter
	@Getter
	private String prefix = "", finalPrefixFormatting = "", finalColor, finalFormat, firstColor;
	
	public void reload() {
		updatePrefix();
		updatePrefixFormatting();
	}
	
	public abstract FileConfiguration getConfig();
	
	public void updatePrefix() {
		setPrefix(MessagingUtil.format(getConfig().getString("prefix")));
		this.updatePrefixFormatting();
	}
	
	private void updatePrefixFormatting() {
		this.finalColor = "";
		this.finalFormat = "";
		this.firstColor = "";
		if (this.prefix.length() > 1) {
			for (int index = this.prefix.length(); index > 1; index--) {
				String bit = this.prefix.substring(index - 2, index);
				if (bit.startsWith("§")) {
					int chNum = bit.toLowerCase().charAt(1);
					if ((97 <= chNum && chNum <= 102) || (48 <= chNum && chNum <= 57) || chNum == 114) {
						if (finalColor.equals("")) {
							finalColor = bit;
						}
						firstColor = bit;
					}
					if (107 <= chNum && chNum <= 112) {
						if (finalFormat.equals("")) {
							finalFormat += bit;
						}
					}
					
				}
			}
		}
		this.finalPrefixFormatting = this.finalColor + this.finalFormat;
	}
	
	public static String makeSpigotSafe(String arg)
	{
		arg = MessagingUtil.format(arg);
		
		String        color   = null;
		StringBuilder builder = new StringBuilder();
		for (String sentence : arg.split("\\n"))
		{
			for (String word : sentence.split("\\s"))
			{
				if (word.startsWith("§")) color = word.substring(0, 2);
				else if (color != null) word = color + word;
				
				builder.append(word);
				builder.append(" ");
			}
			
			if (builder.length() > 0) builder.delete(builder.length() - 1, builder.length());
			builder.append("\n");
		}
		
		return builder.length() > 0 ? builder.substring(0, builder.length() - 2) : builder.toString();
	}
	
	public String getString(String path) {
		String message = "";
		if (getConfig().isList(path)) {
			for (String line : getConfig().getStringList(path)) {
				message += line + "\n";
			}
			message = message.substring(0, message.length() - 1);
		} else {
			message = getConfig().getString(path);
		}
		
		return message;
	}
	
	public String getMessage(String path, String... placeholders) {
		return placeholders(getString(path), placeholders);
	}
	
	public void sendMessage(CommandSender sender, String msg, String... placeholders) {
		sender.sendMessage(this.placeholders(msg, placeholders));
	}
	
	public void sendMessageAtPath(CommandSender sender, String path, String... placeholders) {
		sendMessage(sender, getString(path), placeholders);
	}
	
	public void sendNoPermissionMessage(CommandSender sender) {
		if (getConfig().isSet("messages.no-permission")) {
			sendMessageAtPath(sender, "messages.no-permission");
		} else if (getConfig().isSet("no-permission")) {
			sendMessageAtPath(sender, "no-permission");
		} else {
			sendMessage(sender, "&cYou don't have permission for that");
		}
	}
	
	public void sendConfigReloadedMessage(CommandSender sender) {
		if (getConfig().isSet("messages.config-reloaded")) {
			sendMessageAtPath(sender, "messages.config-reloaded");
		} else if (getConfig().isSet("config-reloaded")) {
			sendMessageAtPath(sender, "config-reloaded");
		} else {
			sendMessage(sender, "{PREFIX}Configuration file reloaded successfully");
		}
	}
	
	public void broadcastMessageWithPerm(String msg, String permission, String... placeholders) {
		Bukkit.broadcast(this.placeholders(msg, placeholders), permission);
	}
	
	public void broadcastMessageWithPermAtPath(String path, String permission, String... placeholders) {
		broadcastMessageWithPerm(getConfig().getString(path), permission, placeholders);
	}
	
	public void broadcastMessage(String msg, String... placeholders) {
		Bukkit.broadcastMessage(this.placeholders(msg, placeholders));
	}
	
	public void broadcastMessageAtPath(String path, String... placeholders) {
		broadcastMessage(getString(path), placeholders);
	}
	
	public String getProgressBar(double progress, double maxProgress, int progressBarCount, String completedColor, String missingColor, String progressBarChar) {
		int completedProgress = (int) Math.floor((progress / maxProgress) * progressBarCount);
		completedProgress = completedProgress > progressBarCount ? progressBarCount : completedProgress;
		int uncompletedProgress = progressBarCount - completedProgress;
		
		String progressBar = completedColor;
		for (int i = 0; i < completedProgress; i++) {
			progressBar += progressBarChar;
		}
		
		if (uncompletedProgress > 0) {
			progressBar += missingColor;
			for (int i = 0; i < uncompletedProgress; i++) {
				progressBar += progressBarChar;
			}
		}
		return MessagingUtil.format(progressBar);
	}
	
	public static String replacePlaceholders(String msg, String... placeholders) {
		for (int i = 0; i < placeholders.length - 1; i += 2) {
			String placeholder = placeholders[i];
			if (placeholder.charAt(0) != '{') {
				placeholder = "{" + placeholder;
			}
			if (placeholder.charAt(placeholder.length() - 1) != '}') {
				placeholder += "}";
			}
			placeholders[i] = placeholder;
		}
		
		for (int i = 0; i < placeholders.length - 1; i += 2) {
			msg = msg.replace(placeholders[i], placeholders[i + 1]);
		}
		
		return msg;
	}
	
	public static String format(String msg, String... placeholders) {
		return StringEscapeUtils.unescapeJava(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(msg, placeholders)));
	}
	
	public String placeholders(String msg, String... placeholders) {
		return StringEscapeUtils.unescapeJava(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(msg, placeholders).replace("{PREFIX}", this.prefix)));
	}
	
}
