package rorys.library.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import rorys.library.events.NPCInteractEvent;
import rorys.library.managers.NPCManager;
import rorys.library.npcs.NPC;

public class NPCInteractListener implements Listener {

    public NPCInteractListener(JavaPlugin plugin, NPCManager npcManager, ProtocolManager protocolManager) {

        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                    Player p = event.getPlayer();
                    PacketContainer packetContainer = event.getPacket();
                    NPC npc = npcManager.getNPC(packetContainer.getIntegers().getValues().get(0));
                    if (npc != null) {
                        plugin.getServer().getPluginManager().callEvent(new NPCInteractEvent(npc, p, packetContainer.getEntityUseActions().getValues().get(0)));
                    }
                }
            }
        });

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

}
