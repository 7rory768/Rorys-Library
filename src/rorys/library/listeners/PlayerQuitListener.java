package rorys.library.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rorys.library.managers.NPCManager;

public class PlayerQuitListener implements Listener {

    private final NPCManager npcManager;

    public PlayerQuitListener(JavaPlugin plugin, NPCManager npcManager) {
        this.npcManager = npcManager;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.npcManager.hideNPCs(e.getPlayer());
    }

}
