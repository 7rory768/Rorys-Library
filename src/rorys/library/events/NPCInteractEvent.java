package rorys.library.events;

import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import rorys.library.npcs.NPC;

public class NPCInteractEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final NPC npc;
    private final Player p;
    private final EnumWrappers.EntityUseAction action;

    public NPCInteractEvent(NPC npc, Player p, EnumWrappers.EntityUseAction action) {
        this.npc = npc;
        this.p = p;
        this.action = action;
    }

    public NPC getNPC() {
        return this.npc;
    }

    public Player getPlayer() {
        return this.p;
    }

    public EnumWrappers.EntityUseAction getAction() {
        return this.action;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
