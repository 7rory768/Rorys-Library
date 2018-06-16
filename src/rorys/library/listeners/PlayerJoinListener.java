package rorys.library.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rorys.library.managers.NPCManager;

public class PlayerJoinListener implements Listener {

    private final NPCManager npcManager;

    public PlayerJoinListener(JavaPlugin plugin, NPCManager npcManager) {
        this.npcManager = npcManager;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        this.npcManager.showNPCs(e.getPlayer());
    }

}
