package rorys.library.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rorys.library.configs.PlayerConfigs;
import rorys.library.util.MessagingUtil;

import java.util.List;
import java.util.UUID;

public class MessageQueuer implements Listener {

    private final JavaPlugin plugin;
    private final PlayerConfigs playerConfigs;
    private final MessagingUtil messagingUtil;
    
    public MessageQueuer(JavaPlugin plugin, PlayerConfigs playerConfigs, MessagingUtil messagingUtil) {
        this.plugin = plugin;
        this.playerConfigs = playerConfigs;
        this.messagingUtil = messagingUtil;
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void queueMessage(OfflinePlayer offlinePlayer, String message) {
        UUID uuid = offlinePlayer.getUniqueId();
        this.queueMessage(uuid, message);
    }
    
    public void queueMessage(UUID uuid, String message) {
        List<String> queuedMessages = this.playerConfigs.getConfig(uuid).getStringList("queued-messages");
        queuedMessages.add(message);
        this.playerConfigs.getConfig(uuid).set("queued-messages", queuedMessages);
        this.playerConfigs.saveConfig(uuid);
        this.playerConfigs.reloadConfig(uuid);
    }
    
    public void sendQueuedMessages(Player p) {
        List<String> queuedMessages = this.playerConfigs.getConfig(p).getStringList("queued-messages");
        for (String message : queuedMessages) {
            this.messagingUtil.sendMessage(p, message);
        }
        this.playerConfigs.getConfig(p).set("queued-messages", null);
        this.playerConfigs.saveConfig(p);
        this.playerConfigs.reloadConfig(p);
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendQueuedMessages(e.getPlayer());
            }
        }.runTaskLaterAsynchronously(plugin, 5L);
    }
    
}
