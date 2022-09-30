package roryslibrary.util;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rory on 6/22/2017.
 */
public abstract class MessagingUtil {
	
	@Setter
	@Getter
	protected String rawPrefix, prefix = "", finalPrefixFormatting = "", finalColor, finalFormat, firstColor;
	protected static Pattern HEX_PATTERN = Pattern.compile("#[A-Fa-f0-9]{6}");
	protected static Pattern HEX_PATTERN_INCLUDING_SET = Pattern.compile("((?:#[A-Fa-f0-9]{6})|(§x(?:§[A-Fa-f0-9]){6}))+");
	protected static boolean supportsHex;
	
	static {
		try {
			Class.forName("net.minecraft.server.v1_8_R3.ChatHexColor");
			supportsHex = true;
		} catch (Exception e) {
			supportsHex = Version.isRunningMinimum(Version.v1_13);
		}
	}
	
	public void reload() {
		updatePrefix();
		updatePrefixFormatting();
	}
	
	public abstract FileConfiguration getConfig();
	
	public void updatePrefix() {
		this.rawPrefix = getConfig().getString("prefix");
		this.prefix = MessagingUtil.format(rawPrefix);
		this.updatePrefixFormatting();
	}
	
	private void updatePrefixFormatting() {
		this.finalColor = "";
		this.finalFormat = "";
		this.firstColor = "";
		
		Matcher matcher = HEX_PATTERN.matcher(rawPrefix);
		int firstHexIndex = -1, finalHexIndex = -1;
		
		while (matcher.find()) {
			if (firstColor.isEmpty()) {
				firstColor = matcher.group();
				firstHexIndex = matcher.start();
			}
			
			finalColor = matcher.group();
			finalHexIndex = matcher.start();
		}
		
		if (this.rawPrefix.length() > 1) {
			for (int index = this.rawPrefix.length(); index > 1; index--) {
				
				if (index == firstHexIndex || index == finalHexIndex) continue;
				
				String bit = this.rawPrefix.substring(index - 2, index);
				if (bit.startsWith("§") || bit.startsWith("&")) {
					int chNum = bit.toLowerCase().charAt(1);
					if ((97 <= chNum && chNum <= 102) || (48 <= chNum && chNum <= 57) || chNum == 114) {
						if (finalColor.equals("")) {
							finalColor = bit;
						} else if (finalHexIndex > -1 && index > finalHexIndex) {
							finalColor = bit;
							finalHexIndex = -1;
						}
						
						if (firstHexIndex == -1 || index < firstHexIndex) firstColor = bit;
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
	
	public static String getFirstColor(String msg) {
		String firstColor = "";
		int firstHexIndex = -1;
		
		Matcher matcher = HEX_PATTERN.matcher(msg);
		
		if (matcher.find()) {
			firstColor = matcher.group();
			firstHexIndex = matcher.start();
		}
		
		if (msg.length() > 1) {
			for (int index = msg.length(); index > 1; index--) {
				if (index == firstHexIndex) continue;
				
				String bit = msg.substring(index - 2, index);
				if (bit.startsWith("§") || bit.startsWith("&")) {
					int chNum = bit.toLowerCase().charAt(1);
					if ((97 <= chNum && chNum <= 102) || (48 <= chNum && chNum <= 57) || chNum == 114) {
						if (firstHexIndex == -1 || index < firstHexIndex)
							firstColor = bit;
					}
				}
			}
		}
		
		return firstColor;
	}
	
	String getBrightestColor(String msg)
	{
		String  brightestColor    = "&f";
		int     highestBrightness = -1;
		Matcher matcher           = HEX_PATTERN_INCLUDING_SET.matcher(msg);
		
		while (matcher.find())
		{
			String string = matcher.group().replace("§x", "#").replace("§", "");
			Color color      = ChatColor.of(string).getColor();
			int   brightness = (int) (Math.max(color.getRed(), Math.max(color.getBlue(), color.getGreen())) / 2.55D);
			
			if (brightness > highestBrightness)
			{
				brightestColor = string;
				highestBrightness = brightness;
			}
		}
		
		msg = matcher.replaceAll("");
		
		if (msg.length() > 1)
		{
			for (int index = 0; index < msg.length() - 1; index++)
			{
				String bit = msg.substring(index, index + 2);
				if (bit.startsWith("§") || bit.startsWith("&"))
				{
					char chNum = bit.toLowerCase().charAt(1);
					if (('a' <= chNum && chNum <= 'f' || '0' <= chNum && chNum <= '9' || chNum == 'r'))
					{
						Color color      = ChatColor.getByChar(chNum).getColor();
						int   brightness = (int) (Math.max(color.getRed(), Math.max(color.getBlue(), color.getGreen())) / 2.55D);
						
						if (brightness > highestBrightness)
						{
							brightestColor = bit;
							highestBrightness = brightness;
						}
					}
				}
			}
		}
		
		return brightestColor;
	}
	
	public static String getLastColor(String msg) {
		String finalColor = "";
		int finalHexIndex = -1;
		
		Matcher matcher = HEX_PATTERN.matcher(msg);
		
		while (matcher.find()) {
			finalColor = matcher.group();
			finalHexIndex = matcher.start();
		}
		
		if (msg.length() > 1) {
			for (int index = msg.length(); index > 1; index--) {
				
				if (index == finalHexIndex) continue;
				
				String bit = msg.substring(index - 2, index);
				if (bit.startsWith("§") || bit.startsWith("&")) {
					int chNum = bit.toLowerCase().charAt(1);
					if ((97 <= chNum && chNum <= 102) || (48 <= chNum && chNum <= 57) || chNum == 114) {
						if (finalColor.equals("")) {
							finalColor = bit;
						} else if (finalHexIndex > -1 && index > finalHexIndex) {
							finalColor = bit;
							finalHexIndex = -1;
						}
					}
				}
			}
		}
		
		return finalColor;
	}
	
	public static String makeSpigotSafe(String arg) {
		arg = MessagingUtil.format(arg);
		
		String color = null;
		StringBuilder builder = new StringBuilder();
		for (String sentence : arg.split("\n")) {
			for (String word : sentence.split("\\s")) {
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
	
	public static String getProgressBar(double progress, double maxProgress, int progressBarCount, String completedColor, String missingColor, String progressBarChar) {
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
	
	public static String getProgressBar(double progress, double maxProgress, int progressBarCount) {
		return getProgressBar(progress, maxProgress, progressBarCount, ChatColor.GREEN.toString(), ChatColor.RED.toString(), "⬛");
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
		if (supportsHex) {
			Matcher matcher = HEX_PATTERN.matcher(msg);
			
			while (matcher.find()) {
				String match = matcher.group();
				msg = msg.replace(match, ChatColor.of(match).toString());
			}
		}
		
		return StringEscapeUtils.unescapeJava(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(msg, placeholders)));
	}
	
	public static List<String> format(List<String> msg, String... placeholders) {
		for (int i = msg.size() - 1; i >= 0; i--)
			msg.add(i, format(msg.remove(i), placeholders));
		
		return msg;
	}
	
	public String placeholders(String msg, String... placeholders) {
		return format(msg, placeholders).replace("{PREFIX}", this.prefix);
	}
	
}
