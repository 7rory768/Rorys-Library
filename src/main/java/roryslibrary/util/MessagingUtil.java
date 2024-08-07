package roryslibrary.util;

import java.io.StringWriter;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.List;
import java.util.StringJoiner;
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
	
	public static String getBrightestColor(String msg) {
		if (!supportsHex) return getFirstColor(msg);
		
		String brightestColor = "&f";
		int highestBrightness = -1;
		Matcher matcher = HEX_PATTERN_INCLUDING_SET.matcher(msg);
		
		while (matcher.find()) {
			String string = matcher.group().replace("§x", "#").replace("§", "");
			Color color = ChatColor.of(string).getColor();
			if (color == null) continue;
			
			int brightness = (int) (Math.max(color.getRed(), Math.max(color.getBlue(), color.getGreen())) / 2.55D);
			
			if (brightness > highestBrightness) {
				brightestColor = string;
				highestBrightness = brightness;
			}
		}
		
		msg = matcher.replaceAll("");
		
		if (msg.length() > 1) {
			for (int index = 0; index < msg.length() - 1; index++) {
				String bit = msg.substring(index, index + 2);
				if (bit.startsWith("§") || bit.startsWith("&")) {
					char chNum = bit.toLowerCase().charAt(1);
					if ('a' <= chNum && chNum <= 'f' || '0' <= chNum && chNum <= '9') {
						Color color = ChatColor.getByChar(chNum).getColor();
						if (color == null) continue;
						
						int brightness = (int) (Math.max(color.getRed(), Math.max(color.getBlue(), color.getGreen())) / 2.55D);
						
						if (brightness > highestBrightness) {
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
		
		StringBuilder lastColors = null;
		StringJoiner joiner = new StringJoiner("\n");
		
		for (String sentence : arg.split("\n")) {
			if (lastColors != null) {
				sentence = lastColors + sentence;
			}
			joiner.add(sentence);
			
			lastColors = new StringBuilder(ChatColor.RESET.toString());
			int length = sentence.length();
			
			for (int index = length - 1; index > -1; index--) {
				char section = sentence.charAt(index);
				
				if (section == ChatColor.COLOR_CHAR && index < length - 1) {
					char ch = sentence.charAt(index + 1);
					ChatColor color = ChatColor.getByChar(ch);
					
					if (color != null) {
						lastColors.insert(0, color);
						
						if (((int) ch >= 66 && (int) ch <= 97) || ((int) ch >= 48 && (int) ch <= 57) || color == ChatColor.RESET) {
							break;
						}
					}
				}
			}
		}
		
		return joiner.toString();
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
		completedProgress = Math.min(completedProgress, progressBarCount);
		int uncompletedProgress = progressBarCount - completedProgress;
		
		StringBuilder progressBar = new StringBuilder(completedColor);
		for (int i = 0; i < completedProgress; i++) {
			progressBar.append(progressBarChar);
		}
		
		if (uncompletedProgress > 0) {
			progressBar.append(missingColor);
			for (int i = 0; i < uncompletedProgress; i++) {
				progressBar.append(progressBarChar);
			}
		}
		return MessagingUtil.format(progressBar.toString());
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
		msg = unescapeJava(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(msg, placeholders)));
		
		if (supportsHex) {
			Matcher matcher = HEX_PATTERN.matcher(msg);
			
			while (matcher.find()) {
				String match = matcher.group();
				msg = msg.replace(match, ChatColor.of(match).toString());
			}
		}
		
		return msg;
	}

	/**
	 * <p>Unescapes any Java literals found in the {@code String}.
	 *
	 * <p>For example, it will turn a sequence of {@code '\'} and
	 * {@code 'n'} into a newline character, unless the {@code '\'}
	 * is preceded by another {@code '\'}.</p>
	 *
	 * <p>Code is copied from apache commons-lang2 StringEscapeUtils</p>
	 *
	 * @param str the {@code String} to unescape, may be null
	 * @return unescaped result
	 */
	private static String unescapeJava(String str) {
		StringWriter out = new StringWriter(str.length());
		int sz = str.length();
		StringBuilder unicode = new StringBuilder(4);
		boolean hadSlash = false;
		boolean inUnicode = false;
		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);
			if (inUnicode) {
				// if in unicode, then we're reading unicode
				// values in somehow
				unicode.append(ch);
				if (unicode.length() == 4) {
					// unicode now contains the four hex digits
					// which represents our unicode character
					try {
						int value = Integer.parseInt(unicode.toString(), 16);
						out.write((char) value);
						unicode.setLength(0);
						inUnicode = false;
						hadSlash = false;
					} catch (NumberFormatException nfe) {
						throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
					}
				}
				continue;
			}
			if (hadSlash) {
				// handle an escaped value
				hadSlash = false;
				switch (ch) {
					case '\\':
						out.write('\\');
						break;
					case '\'':
						out.write('\'');
						break;
					case '\"':
						out.write('"');
						break;
					case 'r':
						out.write('\r');
						break;
					case 'f':
						out.write('\f');
						break;
					case 't':
						out.write('\t');
						break;
					case 'n':
						out.write('\n');
						break;
					case 'b':
						out.write('\b');
						break;
					case 'u':
					{
						// uh-oh, we're in unicode country....
						inUnicode = true;
						break;
					}
					default :
						out.write(ch);
						break;
				}
				continue;
			} else if (ch == '\\') {
				hadSlash = true;
				continue;
			}
			out.write(ch);
		}
		if (hadSlash) {
			// then we're in the weird case of a \ at the end of the
			// string, let's output it anyway.
			out.write('\\');
		}
		return out.toString();
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
