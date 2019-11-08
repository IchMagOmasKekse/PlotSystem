package me.teamdream.de.inventory;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.teamdream.de.plotmanager.PlotProfile;

public class PlotBuyInventory {
	
	public static HashMap<Player, Inventory> invs = new HashMap<Player, Inventory>();
	
	public Inventory getInventory(Player player, PlotProfile profile) {
		if(invs.containsKey(player)) return null;
		Inventory inv = Bukkit.createInventory(null, 9, "Plot kaufen");
		ItemStack item = null;
		ItemMeta meta = null;
		
		item = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§cKauf abbrechen");
		item.setItemMeta(meta);
		inv.setItem(8, item);
		
		item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
		meta.setDisplayName("§aKauf bestätigen");
		item.setItemMeta(meta);
		inv.setItem(6, item);

		item = new ItemStack(Material.BOOK, 1);
		meta.setDisplayName("§7PlotID: §f"+profile.plotid.getID());
		item.setItemMeta(meta);
		inv.setItem(1, item);
		
		item = new ItemStack(Material.PAPER, 1);
		meta.setDisplayName("§7Preis: §f"+profile.preis+"€");
		item.setItemMeta(meta);
		inv.setItem(0, item);
		
		invs.put(player, inv);
		return inv;
	}
	
}