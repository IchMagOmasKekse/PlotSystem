package me.teamdream.de.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.teamdream.de.PlotSystem;
import me.teamdream.de.plotmanager.PlotManager;

public class PlotListInventory {
	
	public static HashMap<Player, Inventory> invs = new HashMap<Player, Inventory>();
	private PlotManager plotmanager = PlotSystem.getInstance().getPlotManager();
	
	public Inventory getInventory(Player player) {
		if(invs.containsKey(player)) return null;
		ArrayList<String> lore = new ArrayList<String>();
		Inventory inv = Bukkit.createInventory(null, 54, "Plots");
		ItemStack item = null;
		ItemMeta meta = null;
		
		item = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("�7Seite zur�ck");
		item.setItemMeta(meta);
		inv.setItem(45, item);
		
		meta.setDisplayName("�7N�chste Seite");
		item.setItemMeta(meta);
		inv.setItem(53, item);

		item = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
		meta.setDisplayName("�7Schlie�en");
		item.setItemMeta(meta);
		inv.setItem(49, item);
		
		lore.add("�7Linksklick, um dich dorthin zu teleportieren");
		
		int plotnumber = 0;
		if(plotmanager.plots.isEmpty() == false) {
			for(String p : plotmanager.plots.keySet()) {
				if(plotnumber < 45) {
					if(plotmanager.plots.get(p) != null && plotmanager.isCorrectlyRegistered(plotmanager.plots.get(p).plotid)) {						
						lore.clear();
						lore.add("�7Owner: �f"+Bukkit.getOfflinePlayer(plotmanager.getOwner(plotmanager.plots.get(p).plotid)).getName());
						item = new ItemStack(Material.OAK_FENCE, 1);
						meta = item.getItemMeta();
						meta.setLore(lore);
						meta.setDisplayName("�b"+p);
						item.setItemMeta(meta);
						inv.setItem(plotnumber, item);
						plotnumber++;
					}
				}else break;
			}
		}
		if(plotnumber == 0) {
			item = new ItemStack(Material.BARRIER);
			meta = item.getItemMeta();
			meta.setDisplayName("�cKeine Plots vorhanden!");
			item.setItemMeta(meta);
			for(int i = 0; i < 45; i++) inv.setItem(i, item);
		}
		invs.put(player, inv);
		return inv;
	}
	
}