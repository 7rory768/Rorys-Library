package rorys.library.npcs;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rorys.library.util.MessagingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    private EntityPlayer entityPlayer;
    private UUID uuid;
    private String name, value, signature, displayName;
    private Location location;
    private List<String> interactCommands = new ArrayList<>();

    public NPC(EntityPlayer entityPlayer, UUID uuid, String name, String value, String signature, Location location, String displayName) {
        this.entityPlayer = entityPlayer;
        this.uuid = uuid;
        this.name = name;
        this.value = value;
        this.signature = signature;
        this.location = location;
        this.displayName = displayName;
    }

    public EntityPlayer getEntityPlayer() {
        return this.entityPlayer;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setCommands(List<String> commands) {
        this.interactCommands = commands;
    }

    public void addCommand(String command) {
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        if (command.startsWith("PLAYER:/")) {
            command = "PLAYER:" + command.substring(8);
        }

        this.interactCommands.add(command);
    }

    public void removeCommand(int num) {
        this.interactCommands.remove(num);
    }

    public List<String> getCommands() {
        return this.interactCommands;
    }

    public void runCommands(Player p) {
        String name = p.getName();
        CommandSender console = Bukkit.getConsoleSender();
        for (String command : this.interactCommands) {
            command.replace("{PLAYER}", name);

            if (command.startsWith("PLAYER:")) {
                p.performCommand(command.substring(7));
            } else {
                Bukkit.dispatchCommand(console, command);
            }

        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.entityPlayer.setCustomName(MessagingUtil.format(name));
    }

    public String getSignature() {
        return this.signature;
    }

    public String getValue() {
        return this.value;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Location getLocation() {
        return this.location;
    }
}
