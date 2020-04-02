package rorys.library;

import org.bukkit.plugin.java.JavaPlugin;
import rorys.library.util.ItemUtil;
import rorys.library.util.MessagingUtil;

/**
 * Created by Rory on 6/22/2017.
 */
public class RorysLibrary {

    private final JavaPlugin plugin;
    private final MessagingUtil messagingUtil;
    private final ItemUtil itemUtil;

    public RorysLibrary(JavaPlugin plugin) {
        this.plugin = plugin;
        this.messagingUtil = new MessagingUtil(this.plugin);
        this.itemUtil = new ItemUtil(this.plugin);
    }

    public MessagingUtil getMessagingUtil() {
        return this.messagingUtil;
    }

    public ItemUtil getItemUtil() {
        return this.itemUtil;
    }
}
