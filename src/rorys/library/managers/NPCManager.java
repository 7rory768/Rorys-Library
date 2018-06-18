package rorys.library.managers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rorys.library.configs.CustomConfig;
import rorys.library.npcs.NPC;
import rorys.library.util.SkinUtil;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class NPCManager {

    private final JavaPlugin plugin;
    private final CustomConfig npcsConfig;

    private HashSet<NPC> npcs = new HashSet<>();

    public NPCManager(JavaPlugin plugin, CustomConfig npcsConfig) {
        this.plugin = plugin;
        this.npcsConfig = npcsConfig;

        this.loadNPCs();
        this.showNPCs();
    }

    public void loadNPCs() {
        FileConfiguration npcsConfigFile = this.npcsConfig.getConfig();

        for (String id : npcsConfigFile.getConfigurationSection("npcs").getKeys(false)) {
            String path = "npcs." + id + ".";

            UUID uuid = UUID.fromString(npcsConfigFile.getString(path + "uuid"));
            String name = npcsConfigFile.getString(path + "name");
            String value = npcsConfigFile.getString(path + "value");
            String signature = npcsConfigFile.getString(path + "signature");
            String displayName = npcsConfigFile.getString(path + "display-name");
            List<String> commands = npcsConfigFile.getStringList(path + ".commands");

            World world = Bukkit.getServer().getWorld(npcsConfigFile.getString(path + "location.world"));
            double x = npcsConfigFile.getDouble(path + "location.x");
            double y = npcsConfigFile.getDouble(path + "location.y");
            double z = npcsConfigFile.getDouble(path + "location.z");
            float yaw = Float.valueOf(npcsConfigFile.getString(path + "location.yaw"));
            float pitch = Float.valueOf(npcsConfigFile.getString(path + "location.pitch"));

            Location location = new Location(world, x, y, z, yaw, pitch);

            NPC npc = this.spawnNPCWithValueAndSignature(location, uuid, name, value, signature, displayName);
            npc.setCommands(commands);
        }
    }

    public void saveNPCs() {
        FileConfiguration npcsConfigFile = this.npcsConfig.getConfig();
        npcsConfigFile.set("npcs", null);
        npcsConfigFile.createSection("npcs");

        int id = 1;
        for (NPC npc : this.npcs) {
            String path = "npcs." + id++ + ".";

            EntityPlayer entityPlayer = npc.getEntityPlayer();
            UUID uuid = entityPlayer.getUniqueID();
            String name = npc.getName();
            String displayName = npc.getDisplayName();
            List<String> commands = npc.getCommands();
            Location location = npc.getLocation();

            npcsConfigFile.set(path + "uuid", uuid.toString());
            npcsConfigFile.set(path + "name", name);
            npcsConfigFile.set(path + "value", npc.getValue());
            npcsConfigFile.set(path + "signature", npc.getSignature());
            npcsConfigFile.set(path + "display-name", displayName);
            npcsConfigFile.set(path + "commands", commands);
            npcsConfigFile.set(path + "location.world", location.getWorld().getName());
            npcsConfigFile.set(path + "location.x", location.getX());
            npcsConfigFile.set(path + "location.y", location.getY());
            npcsConfigFile.set(path + "location.z", location.getZ());
            npcsConfigFile.set(path + "location.yaw", location.getYaw());
            npcsConfigFile.set(path + "location.pitch", location.getPitch());
        }

        this.npcsConfig.saveConfig();
        this.npcsConfig.reloadConfig();
    }

    public UUID getUUID(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p != null) {
            return p.getUniqueId();
        } else {
            OfflinePlayer offlineP = Bukkit.getOfflinePlayer(name);
            if (offlineP != null && offlineP.hasPlayedBefore()) {
                return offlineP.getUniqueId();
            }
        }
        return null;
    }

    public NPC spawnNPCWithName(Location loc, String name, String displayName) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        UUID uuid = this.getUUID(name);
        if (uuid == null) {
            uuid = SkinUtil.getUUIDFromName(name);
        }

        String[] valuesAndSignature = SkinUtil.getValueAndSignature(uuid);
        String value = valuesAndSignature[0], signature = valuesAndSignature[1];

        GameProfile gameProfile = new GameProfile(uuid, name);
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));

        for (String propertyName : gameProfile.getProperties().keySet()) {
            Bukkit.broadcastMessage("property: " + propertyName);
        }

        EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        entityPlayer.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        NPC npc = new NPC(entityPlayer, uuid, name, value, signature, loc, displayName);
        this.npcs.add(npc);

        this.showNPC(npc);

        return npc;
    }

    public NPC spawnNPCWithValueAndSignature(Location loc, UUID uuid, String name, String value, String signature, String displayName) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(uuid, name);
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));
        EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        entityPlayer.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        NPC npc = new NPC(entityPlayer, uuid, name, value, signature, loc, displayName);
        this.npcs.add(npc);
        Bukkit.broadcastMessage("entityPlayer id: " + entityPlayer.getId());

        this.showNPC(npc);

        return npc;
    }

    public void showNPC(NPC npc) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.showNPC(p, npc);
        }
    }


    public void showNPCs() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.showNPCs(p);
        }
    }

    public void showNPCs(Player p) {
        for (NPC npc : this.npcs) {
            this.showNPC(p, npc);
        }
    }

    public void showNPC(Player p, NPC npc) {
        EntityPlayer entityPlayer = npc.getEntityPlayer();
        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((180.0F * 256.0F) / 360.0F)));

        new BukkitRunnable() {
            @Override
            public void run() {
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
            }
        }.runTaskLaterAsynchronously(this.plugin, 5L);

    }

    public void hideNPCs() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.hideNPCs(p);
        }
    }

    public void hideNPCs(Player p) {
        for (NPC npc : this.npcs) {
            this.hideNPC(p, npc);
        }
    }

    public void hideNPC(Player p, NPC npc) {
        EntityPlayer entityPlayer = npc.getEntityPlayer();
        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
    }

    public NPC getNPC(int id) {
        for (NPC npc : this.npcs) {
            if (npc.getEntityPlayer().getId() == id) {
                return npc;
            }
        }
        return null;
    }
}
