package roryslibrary.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public class SkinUtil {
	
	private static JsonParser parser = new JsonParser();
	
	private static HashMap<String, String> skinCache = new HashMap<>();
	private static HashMap<UUID, String> nameCache = new HashMap<>();
	private static HashMap<String, UUID> uuidCache = new HashMap<>();
	
	public static UUID getUUIDFromName(String name) {
		return getUUIDFromName(name, false);
	}
	
	public static UUID getUUIDFromName(String name, boolean checkAPI) {
		if (uuidCache.containsKey(name.toLowerCase())) return uuidCache.get(name.toLowerCase());
		
		OfflinePlayer player = Bukkit.getPlayer(name);
		if (player != null) {
			uuidCache.putIfAbsent(player.getName().toLowerCase(), player.getUniqueId());
			nameCache.putIfAbsent(player.getUniqueId(), player.getName());
			return player.getUniqueId();
		} else {
			player = Bukkit.getOfflinePlayer(name);
			
			if (player.hasPlayedBefore()) {
				uuidCache.putIfAbsent(player.getName().toLowerCase(), player.getUniqueId());
				nameCache.putIfAbsent(player.getUniqueId(), player.getName());
				return player.getUniqueId();
			}
		}
		
		if (checkAPI) {
			try {
				URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
				InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
				JsonObject json = SkinUtil.parser.parse(reader_0).getAsJsonObject();
				
				String uuidString = json.get("id").getAsString();
				uuidString = uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-" + uuidString.substring(12, 16) + "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20);
				UUID uuid = UUID.fromString(uuidString);
				
				name = json.get("name").getAsString();
				
				uuidCache.put(name.toLowerCase(), uuid);
				nameCache.put(uuid, name);
				
				return uuid;
			} catch (Exception e) {
				//
			}
		}
		
		return null;
	}
	
	public static String getName(UUID uuid) {
		return getName(uuid, false);
	}
	
	public static String getName(UUID uuid, boolean checkAPI) {
		if (nameCache.containsKey(uuid)) return nameCache.get(uuid);
		
		OfflinePlayer player = Bukkit.getPlayer(uuid);
		if (player != null) {
			uuidCache.put(player.getName().toLowerCase(), uuid);
			nameCache.put(uuid, player.getName());
			return player.getName();
		} else {
			player = Bukkit.getOfflinePlayer(uuid);
			
			if (player.hasPlayedBefore()) {
				uuidCache.put(player.getName().toLowerCase(), uuid);
				nameCache.put(uuid, player.getName());
				return player.getName();
			}
		}
		
		if (checkAPI) {
			try {
				String uuidStr = uuid.toString().replace("-", "");
				URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidStr + "?unsigned=false");
				InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
				JsonObject jsonObject = new JsonParser().parse(reader_1).getAsJsonObject();
				
				String name = jsonObject.get("name").getAsString();
				
				uuidCache.put(name.toLowerCase(), uuid);
				nameCache.put(uuid, name);
				
				return name;
			} catch (Exception e) {
				//
			}
		}
		
		return "NULL";
	}
	
	public static String[] getValueAndSignature(UUID uuid) {
		try {
			String uuidStr = uuid.toString().replace("-", "");
			URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidStr + "?unsigned=false");
			InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
			JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
			String texture = textureProperty.get("value").getAsString();
			String signature = textureProperty.get("signature").getAsString();
			String[] args = new String[2];
			args[0] = texture;
			args[1] = signature;
			return args;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getSkinValue(String url) {
		url = url.toLowerCase();
		
		if (!skinCache.containsKey(url)) {
			try {
				final Document doc = Jsoup.connect(url).get();
				final Element element = doc.getElementById("UUID-Value");
				
				if (element == null) return url;
				
				skinCache.put(url, element.text());
			} catch (Exception ex) {
				return url;
			}
		}
		
		return skinCache.get(url);
	}
}
