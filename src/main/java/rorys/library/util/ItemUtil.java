package rorys.library.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Rory on 6/30/2017.
 */
public class ItemUtil {

	private JavaPlugin plugin;

	public ItemUtil(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public static int getX(int slot) {
		return slot % 9 + 1;
	}

	public static int getY(int slot) {
		return slot / 9 + 1;
	}

	public static SkullMeta applyCustomHead(ItemMeta itemMeta, UUID uuid) {
		SkullMeta skullMeta = (SkullMeta) itemMeta;
		skullMeta.setOwner(SkinUtil.getName(uuid));
		return skullMeta;
	}

	public static boolean isSimilar(ItemStack o, ItemStack o1) {
		try {
			if ((o == null && o1 != null) || (o != null && o1 == null)) {
				return false;
			}
			if (o != null) {
				return o.isSimilar(o1);
			}
		} catch (Exception e) {

			if (o.getType() != o1.getType() || o.getDurability() != o1.getDurability()) {
				return false;
			}

			ItemMeta oMeta = o.getItemMeta(), o1Meta = o1.getItemMeta();

			if (oMeta.hasDisplayName() != o1Meta.hasDisplayName() || (oMeta.hasDisplayName() && !oMeta.getDisplayName().equals(o1Meta.getDisplayName())) || oMeta.hasLore() != o1Meta.hasLore() || (oMeta.hasLore() && !oMeta.getLore().equals(o1Meta.getLore()))) {
				return false;
			}
		}
		return true;
	}

	public static int getInventorySpace(Inventory inventory, ItemStack item) {
		int space    = 0;
		int itemSize = item.getAmount();
		for (int invSlot = 0; invSlot < inventory.getSize(); invSlot++) {
			ItemStack invItem = inventory.getItem(invSlot);
			if (invItem == null || invItem.getType() == Material.AIR) {
				space = itemSize;
			} else if (invItem.isSimilar(item)) {
				space += Math.min(invItem.getMaxStackSize() - invItem.getAmount(), itemSize);
			}

			if (space >= itemSize) {
				space = itemSize;
				break;
			}
		}
		return space;
	}

	public static boolean itemIsReal(ItemStack item) {
		return item != null && item.getType() != Material.AIR;
	}

	public static void addDurabilityGlow(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemMeta);
	}

	public static ItemStack fillPlaceholders(ItemStack item, String... placeholders) {
		for (int i = 0; i < placeholders.length - 1; i += 2) {
			String placeholder = placeholders[i];
			if (placeholder.charAt(0) != '{') {
				placeholder = "{" + placeholder;
			}
			if (placeholder.charAt(placeholder.length() - 1) != '}') {
				placeholder += "}";
			}
			placeholders[i] = placeholder;
		}

		ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta.hasDisplayName()) {
			String name = itemMeta.getDisplayName();
			for (int i = 0; i < placeholders.length - 1; i += 2) {
				name = name.replace(placeholders[i], placeholders[i + 1]);
			}
			itemMeta.setDisplayName(MessagingUtil.format(name));
		}

		if (itemMeta.hasLore()) {
			List<String> lore = new ArrayList<>();
			for (String line : itemMeta.getLore()) {
				for (int i = 0; i < placeholders.length - 1; i += 2) {
					line = line.replace(placeholders[i], placeholders[i + 1]);
				}
				lore.add(MessagingUtil.format(line));
			}
			itemMeta.setLore(lore);
		}

		item.setItemMeta(itemMeta);
		return item;
	}

	public static void storeItemStack(ItemStack item, FileConfiguration config, String path) {
		if (!path.endsWith(".")) {
			path += ".";
		}
		config.set(path + "material", item.getType().name());
		config.set(path + "amount", item.getAmount());
		config.set(path + "data", item.getDurability());

		ItemMeta itemMeta = item.getItemMeta();

		if (itemMeta.hasDisplayName()) {
			config.set(path + "name", itemMeta.getDisplayName());
		}

		if (itemMeta.hasLore()) {
			config.set(path + "lore", itemMeta.getLore());
		}

		if (itemMeta.hasEnchants()) {
			List<String> enchants = new ArrayList<String>();
			for (Map.Entry<Enchantment, Integer> enchant : itemMeta.getEnchants().entrySet()) {
				enchants.add(enchant.getKey().getName() + ":" + enchant.getValue());
				config.set(path + "enchants", enchants);

			}
		}

		List<String> itemFlags = new ArrayList<>();
		for (ItemFlag itemFlag : itemMeta.getItemFlags()) {
			itemFlags.add(itemFlag.name());
		}
		if (!itemFlags.isEmpty()) {
			config.set(path + "item-flags", itemFlags);
		}

		if ((item.getType().name().equals("SKULL_ITEM") || item.getType().name().equals("PLAYER_HEAD"))) {
			SkullMeta skullMeta = (SkullMeta) itemMeta;
			config.set(path + "skull-owner", skullMeta.getOwner());
		}
	}

	public ItemStack getItemStack(String path) {
		return ItemUtil.getItemStack(this.plugin.getConfig(), path);
	}

	public static ItemStack getItemStack(FileConfiguration config, String path) {
		ItemStack item;
		if (!path.endsWith(".")) {
			path += ".";
		}
		Material mat;
		String   matString = config.getString(path + "material", "NULL").toUpperCase();
		try {
			mat = Material.valueOf(matString);
		} catch (IllegalArgumentException e) {
			// Allow compatibility for < 1.13 configs
			if (matString.equals("PLAYER_HEAD")) {
				mat = Material.valueOf("SKULL_ITEM");
			} else if (matString.equalsIgnoreCase("GRAY_STAINED_GLASS_PANE")) {
				mat = Material.valueOf("STAINED_GLASS_PANE");
			} else {
				String pluginName = ChatColor.stripColor(MessagingUtil.format(config.getString("prefix", "ItemUtil").split("\\s")[0]));
				Bukkit.getLogger().info("[" + pluginName + "] Invalid material '" + matString + "' @ " + path);
				return null;
			}
		}
		int amount = config.getInt(path + "amount", 1);
		item = new ItemStack(mat, amount);
		short data = (short) config.getInt(path + "data", item.getDurability());

		// Allow compatibility for < 1.13 configs
		if (matString.equals("GRAY_STAINED_GLASS_PANE")) {
			data = (short) 7;
		}

		item.setDurability(data);
		ItemMeta itemMeta = item.getItemMeta();
		String   name     = config.getString(path + "name", "");
		if (!name.equals("")) {
			itemMeta.setDisplayName(MessagingUtil.format(name));
		}

		List<String> loreLines = config.getStringList(path + "lore");
		if (!loreLines.isEmpty()) {
			List<String> lore = new ArrayList<>();
			for (String loreLine : loreLines) {
				lore.add(MessagingUtil.format(loreLine));
			}
			itemMeta.setLore(lore);
		}

		List<String> enchants = config.getStringList(path + "enchants");
		for (String enchantInfo : enchants) {
			int         colonIndex  = enchantInfo.indexOf(":");
			Enchantment enchantment = Enchantment.getByName(enchantInfo.substring(0, colonIndex));
			if (enchantment != null) {
				int level = Integer.parseInt(enchantInfo.substring(colonIndex + 1, enchantInfo.length()));
				itemMeta.addEnchant(enchantment, level, true);
			}
		}

		List<String> itemFlags = config.getStringList(path + "item-flags");
		for (String itemFlag : itemFlags) {
			itemMeta.addItemFlags(ItemFlag.valueOf(itemFlag));
		}

		if ((mat.name().equals("PLAYER_HEAD") || mat.name().equals("SKULL_ITEM"))) {
			if (mat.name().equals("SKULL_ITEM") && !config.isSet(path + "data")) {
				item.setDurability((short) 3);
			}
			if (config.isSet(path + "skin-value")) {
				ItemMeta oldItemMeta = itemMeta;
				itemMeta = ItemUtil.applyCustomHead(itemMeta, config.getString(path + "skin-value"));
				if (itemMeta == null) {
					itemMeta = oldItemMeta;
					String pluginName = ChatColor.stripColor(MessagingUtil.format(config.getString("prefix", "ItemUtil").split("\\s")[0]));
					Bukkit.getLogger().info("[" + pluginName + "] Failed to load skull skin-value @ " + path);
				}
			}
		}

		if ((mat == Material.LEATHER_BOOTS || mat == Material.LEATHER_CHESTPLATE || mat == Material.LEATHER_HELMET || mat == Material.LEATHER_LEGGINGS) && config.isSet(path + "color")) {
			LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
			leatherArmorMeta.setColor(Color.fromRGB(config.getInt(path + "color")));
			itemMeta = leatherArmorMeta;
		}

		item.setItemMeta(itemMeta);

		return item;
	}

	public static SkullMeta applyCustomHead(ItemMeta itemMeta, String value) {
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
		gameProfile.getProperties().put("textures", new Property("textures", value));

		SkullMeta skullMeta = (SkullMeta) itemMeta;
		try {
			Field profileField = skullMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(skullMeta, gameProfile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
			return null;
		}

		return skullMeta;
	}

	public int getSlot(String path) {
		return ItemUtil.getSlot(this.plugin.getConfig(), path);
	}

	public static int getSlot(FileConfiguration config, String path) {
		if (!path.endsWith(".")) {
			path += ".";
		}

		int xCord = config.getInt(path + "x-cord", -1);
		int yCord = config.getInt(path + "y-cord", -1);
		if (xCord == -1 || yCord == -1) {
			return config.getInt(path + "slot", 0);
		}
		return ItemUtil.getSlot(xCord, yCord);
	}

	public static int getSlot(int xCord, int yCord) {
		return ((yCord - 1) * 9) + xCord - 1;
	}

}
