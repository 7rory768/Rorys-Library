package roryslibrary.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.UUID;

public class DebugUtil implements Listener
{
	private static JavaPlugin    plugin;
	private static HashSet<UUID> debugPlayers = new HashSet<>();
	
	public void setPlugin(JavaPlugin plugin)
	{
		if (DebugUtil.plugin == null && plugin != null)
			Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		
		DebugUtil.plugin = plugin;
	}
	
	public static void toggleDebug(Player player)
	{
		if (debugPlayers.contains(player.getUniqueId())) debugPlayers.remove(player.getUniqueId());
		else debugPlayers.add(player.getUniqueId());
	}
	
	public static void debug(String arg)
	{
		if (plugin != null)
		{
			DebugUtil.plugin.getLogger().info(arg);
			arg = ChatColor.DARK_GRAY + "[" + org.bukkit.ChatColor.GREEN + plugin.getName() + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + arg;
			
			for (UUID uuid : debugPlayers)
			{
				Player player = Bukkit.getPlayer(uuid);
				if (player != null) player.sendMessage(arg);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreCommand(PlayerCommandPreprocessEvent event)
	{
		if (DebugUtil.plugin != null)
		{
			Player p = event.getPlayer();
			
			if (event.getMessage().toLowerCase().startsWith("/" + plugin.getName().toLowerCase() + " debug"))
			{
				if ((p.hasPermission(plugin.getName().toLowerCase() + ".debug") || p.getUniqueId().toString().equals("30f8109e-7ea7-4ae7-90f4-178bb39cfe31")))
				{
					event.setCancelled(true);
					toggleDebug(p);
					p.sendMessage(MessagingUtil.format("&a&lRorysLibrary &8» &7Debug " + (debugPlayers.contains(p.getUniqueId()) ? "&aenabled" : "&cdisabled") + " &7for &a" + plugin.getName()));
				} else
				{
					p.sendMessage(MessagingUtil.format("&cYou don''t have permission for that"));
				}
			}
		}
	}
	
}
