package rorys.library;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import rorys.library.configs.CustomConfig;
import rorys.library.listeners.NPCInteractListener;
import rorys.library.listeners.PlayerJoinListener;
import rorys.library.listeners.PlayerQuitListener;
import rorys.library.managers.NPCManager;
import rorys.library.util.CustomConfigUtil;
import rorys.library.util.ItemUtil;
import rorys.library.util.MessagingUtil;

/**
 * Created by Rory on 6/22/2017.
 */
public class RorysLibrary {

    private final JavaPlugin plugin;
    private final MessagingUtil messagingUtil;
    private final ItemUtil itemUtil;
    private NPCManager npcManager;
    private CustomConfig npcConfig;

    private ProtocolManager protocolManager;

    public RorysLibrary(JavaPlugin plugin) {
        this(plugin, false);
    }

    public RorysLibrary(JavaPlugin plugin, boolean usingNPCS) {
        this.plugin = plugin;
        this.messagingUtil = new MessagingUtil(this.plugin);
        this.itemUtil = new ItemUtil(this.plugin, this.messagingUtil);
        if (usingNPCS) {
            this.npcConfig = new CustomConfig(this.plugin, "npcs");
            CustomConfigUtil.loadConfig(this.npcConfig);
            this.npcManager = new NPCManager(this.plugin, this.npcConfig);

            PluginManager pluginManager = this.plugin.getServer().getPluginManager();
            if (pluginManager.getPlugin("ProtocolLib") != null && pluginManager.getPlugin("ProtocolLib").isEnabled()) {
                this.protocolManager = ProtocolLibrary.getProtocolManager();

                pluginManager.registerEvents(new NPCInteractListener(this.plugin, this.npcManager, this.protocolManager), this.plugin);
                pluginManager.registerEvents(new PlayerJoinListener(this.plugin, this.npcManager), this.plugin);
                pluginManager.registerEvents(new PlayerQuitListener(this.plugin, this.npcManager), this.plugin);
            }
        }
    }

    public MessagingUtil getMessagingUtil() {
        return this.messagingUtil;
    }

    public ItemUtil getItemUtil() {
        return this.itemUtil;
    }

    public NPCManager getNpcManager() {
        return this.npcManager;
    }
}
