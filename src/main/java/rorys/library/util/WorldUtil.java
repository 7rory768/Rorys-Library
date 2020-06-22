package rorys.library.util;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.nio.file.Files;

public class WorldUtil {

    public static void deleteWorld(String name) {
        File file = new File(Bukkit.getWorldContainer().getPath() + File.separator + name);
        deleteDir(file);
    }

    public static void deleteWorld(World world) {
        File file = new File(Bukkit.getWorldContainer().getPath() + File.separator + world.getName());
        deleteDir(file);
    }

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

}
