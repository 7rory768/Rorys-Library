package rorys.library.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Created by Rory on 6/25/2017.
 */
public class LocationUtil {

    public static Location fromString(String string) {
        string = string.replace(',', '.');
        String[] args = string.split("\\|");
        Location loc = new Location(Bukkit.getWorld(args[0]), Double.valueOf(args[1]), Double.valueOf(args[2]), Double.valueOf(args[3]));
        if (args.length > 4) {
            loc.setYaw(Float.valueOf(args[4]));
            loc.setPitch(Float.valueOf(args[5]));
        }
        return loc;
    }

    public static String toBlockString(Location loc) {
        return loc.getWorld().getName() + "|" + loc.getBlockX() + "|" + loc.getBlockY() + "|" + loc.getBlockZ();
    }

    public static String toString(Location loc) {
        return (loc.getWorld().getName() + "|" + loc.getX() + "|" + loc.getY() + "|" + loc.getZ() + "|" + loc.getYaw() + "|" + loc.getPitch()).replace('.', ',');
    }

    public static Location fromPath(FileConfiguration config, String path) {
        if (!path.endsWith(".")) {
            path += ".";
        }

        World world = Bukkit.getWorld(config.getString(path + "world"));
        double x = config.getDouble(path + "x");
        double y = config.getDouble(path + "y");
        double z = config.getDouble(path + "z");
        if (config.isSet(path + "yaw") && config.isSet(path + "pitch")) {
            float yaw = Float.valueOf(config.getString(path + "yaw"));
            float pitch = Float.valueOf(config.getString(path + "pitch"));
            return new Location(world, x, y, z, yaw, pitch);
        }
        return new Location(world, x, y, z);
    }
    
    public static Location getRandomHighestLocationWithinBorder(World world) {
        WorldBorder worldBorder = world.getWorldBorder();
        int size = (int) worldBorder.getSize();
        Location center = worldBorder.getCenter();
        int minX = (center.getBlockX() - (size / 2)) + 1, minZ = (center.getBlockZ() - (size / 2)) + 1;
        int maxX = (minX + size) - 1, maxZ = (minZ + size) - 1;
        Random random = new Random();
        int randomX = minX + random.nextInt(maxX - minX), randomZ = minZ + random.nextInt(maxZ - minZ);
        Location loc = new Location(world, randomX, 0, randomZ);
        Block block = world.getHighestBlockAt(loc);
        Block lowerBlock = block.getRelative(BlockFace.DOWN);
        if (lowerBlock.getType() == Material.WATER || lowerBlock.getType() == Material.LAVA) {
            return getRandomHighestLocationWithinBorder(world);
        }
        return block.getLocation();
    }
    
    public static Vector getVector(Location origin, Location target, double length) {
        Vector vec = target.toVector().subtract(origin.toVector());
        return vec.normalize().multiply(length);
    }
    
    public static String getEnvironmentString(World.Environment environment) {
        switch (environment) {
            case NETHER:
                return "Nether";
            case NORMAL:
                return "Overworld";
            case THE_END:
                return "End";
        }
        
        return "";
    }
    
}
