package roryslibrary.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import roryslibrary.configs.CustomConfig;
import roryslibrary.util.ItemUtil;
import roryslibrary.util.MessagingUtil;
import roryslibrary.util.Version;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfirmGUI implements Listener {
	
	private final JavaPlugin plugin;
	private final CustomConfig config;
	private Inventory inv;
	private String title;
	
	public ConfirmGUI(JavaPlugin plugin) {
		this(plugin, null);
	}
	
	public ConfirmGUI(JavaPlugin plugin, CustomConfig config) {
		this.plugin = plugin;
		this.config = config;
		
		reload();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void unregister() {
		if (plugin.isEnabled()) HandlerList.unregisterAll(this);
	}
	
	public void onResponse(boolean confirm) {
		unregister();
	}
	
	public abstract void onClose();
	
	public ConfirmGUI title(String title) {
		this.title = title;
		reload();
		return this;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (inv.getViewers().contains(event.getWhoClicked())) {
			int slot = event.getSlot();
			event.setCancelled(true);
			
			if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
				int x = slot - (9 * (slot / 9));
				
				if (x < 4) {
					onResponse(true);
				} else if (x > 4) {
					onResponse(false);
				}
			}
			
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (inv.getViewers().contains(event.getPlayer())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					unregister();
					onClose();
				}
			}.runTaskLater(plugin, 1L);
		}
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().getName().equals(plugin.getName())) {
			for (HumanEntity entity : inv.getViewers()) {
				entity.closeInventory();
			}
		}
	}
	
	public ConfirmGUI open(Player p) {
		p.openInventory(inv);
		return this;
	}
	
	public void reload() {
		FileConfiguration config = this.config == null ? plugin.getConfig() : this.config.getConfig();
		
		List<HumanEntity> viewers = inv == null ? new ArrayList<>() : inv.getViewers();
		
		String title = this.title;
		if (title == null) {
			title = MessagingUtil.format(config.getString("confirm-gui.title", "&aConfirm &7or &cCancel"));
		}
		
		int rows = config.getInt("confirm-gui.rows", 3);
		
		inv = Bukkit.createInventory(null, rows * 9, title);
		
		ItemStack confirmItem = ItemUtil.getItemStack(config, "confirm-gui.confirm-item");
		if (!ItemUtil.itemIsReal(confirmItem)) {
			Material glassMat = Material.valueOf(Version.getVersion().getWeight() < 13 ? "STAINED_GLASS_PANE" : "LIME_STAINED_GLASS_PANE");
			confirmItem = new ItemStack(glassMat, 1, (short) 5);
			ItemMeta meta = confirmItem.getItemMeta();
			meta.setDisplayName(MessagingUtil.format("&aConfirm"));
			confirmItem.setItemMeta(meta);
		}
		
		ItemStack separatorItem = ItemUtil.getItemStack(config, "confirm-gui.separator-item");
		if (!ItemUtil.itemIsReal(separatorItem)) {
			Material glassMat = Material.valueOf(Version.getVersion().getWeight() < 13 ? "STAINED_GLASS_PANE" : "GRAY_STAINED_GLASS_PANE");
			separatorItem = new ItemStack(glassMat, 1, (short) 7);
			ItemMeta meta = separatorItem.getItemMeta();
			meta.setDisplayName(MessagingUtil.format("&f"));
			separatorItem.setItemMeta(meta);
		}
		
		ItemStack cancelItem = ItemUtil.getItemStack(config, "confirm-gui.cancel-item");
		if (!ItemUtil.itemIsReal(cancelItem)) {
			Material glassMat = Material.valueOf(Version.getVersion().getWeight() < 13 ? "STAINED_GLASS_PANE" : "RED_STAINED_GLASS_PANE");
			cancelItem = new ItemStack(glassMat, 1, (short) 14);
			ItemMeta meta = cancelItem.getItemMeta();
			meta.setDisplayName(MessagingUtil.format("&cCancel"));
			cancelItem.setItemMeta(meta);
		}
		
		for (int x = 0; x < 9; x++) {
			ItemStack item;
			
			if (x < 4) {
				item = confirmItem;
			} else if (x == 4) {
				item = separatorItem;
			} else {
				item = cancelItem;
			}
			
			for (int y = 0; y < rows; y++) {
				inv.setItem(y * 9 + x, item);
			}
		}
		
		for (HumanEntity entity : viewers) {
			entity.openInventory(inv);
		}
	}
}
