package roryslibrary.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateNotifier {
	
	//Constants. Customize to your liking.
	private static final String ERR_MSG = "&cUpdate checker failed!";
	private final JavaPlugin javaPlugin;
	private final MessagingUtil messagingUtil;
	private String UPDATE_MSG = "";
	private int ID; //The ID of your resource. Can be found in the resource URL.
	private String localPluginVersion;
	private String spigotPluginVersion;
	private boolean needsUpdate = false;
	
	public UpdateNotifier(final JavaPlugin javaPlugin, MessagingUtil messagingUtil, int ID) {
		this.javaPlugin = javaPlugin;
		this.messagingUtil = messagingUtil;
		this.ID = ID;
		this.localPluginVersion = javaPlugin.getDescription().getVersion();
		
		checkForUpdate();
	}
	
	public String getUpdateMsg() {
		return UPDATE_MSG;
	}
	
	public boolean needsUpdate() {
		return needsUpdate;
	}
	
	public void checkForUpdate() {
		//The request is executed asynchronously as to not block the main thread.
		Bukkit.getScheduler().runTaskAsynchronously(this.javaPlugin, () -> {
			needsUpdate = false;
			//Request the current version of your plugin on SpigotMC.
			try {
				HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + ID).openConnection();
				connection.setRequestMethod("GET");
				this.spigotPluginVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
				int spaceIndex = this.spigotPluginVersion.indexOf(" ");
				if (spaceIndex > -1) {
					this.spigotPluginVersion = this.spigotPluginVersion.substring(0, spaceIndex);
				}
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ERR_MSG));
				e.printStackTrace();
				return;
			}
			
			//Check if the requested version is the same as the one in your plugin.yml.
			if (this.localPluginVersion.equals(this.spigotPluginVersion)) return;
			
			String[] localNums = localPluginVersion.split(".");
			String[] spigotNums = spigotPluginVersion.split(".");
			for (int i = 0; i < Math.max(localNums.length, spigotNums.length); i++) {
				int localNum = localNums.length <= i ? 0 : Integer.parseInt(localNums[i]);
				int spigotNum = spigotNums.length <= i ? 0 : Integer.parseInt(spigotNums[i]);
				
				if (localNum > spigotNum) {
					return;
				} else if (localNum < spigotNum) {
					needsUpdate = true;
					break;
				}
			}
			
			UPDATE_MSG = messagingUtil.placeholders("{PREFIX}Update available! Go to " + messagingUtil.getFirstColor() + "https://www.spigotmc.org/resources/" + ID + "/ &7to update\n"
					+ "{PREFIX}Current version: " + messagingUtil.getFirstColor() + localPluginVersion + "\n"
					+ "{PREFIX}New version: " + messagingUtil.getFirstColor() + spigotPluginVersion);
		});
	}
}