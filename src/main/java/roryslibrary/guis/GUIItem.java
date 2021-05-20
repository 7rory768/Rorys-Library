package roryslibrary.guis;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class GUIItem {
	
	private int slot;
	private ItemStack item;
	private ItemPurpose itemPurpose;
	
	public GUIItem(ItemStack item, ItemPurpose itemPurpose, int slot) {
		this.item = item;
		this.itemPurpose = itemPurpose;
		this.slot = slot;
	}
	
	public GUIItem(int slot, ItemStack item) {
		this.item = item;
		this.slot = slot;
	}
}
