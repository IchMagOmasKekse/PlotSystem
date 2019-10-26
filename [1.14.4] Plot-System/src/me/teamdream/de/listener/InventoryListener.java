package me.teamdream.de.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import me.teamdream.de.PlotSystem;
import me.teamdream.de.inventory.PlotBuyInventory;
import me.teamdream.de.inventory.PlotListInventory;
import me.teamdream.de.plotmanager.PlotManager.PlotID;

public class InventoryListener implements Listener {
	
	public InventoryListener() {
		PlotSystem.getInstance().getServer().getPluginManager().registerEvents(this, PlotSystem.getInstance());
	}
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getClickedInventory() != null) {
			Inventory inv = e.getClickedInventory();
			InventoryView view = e.getView();
			if(e.getWhoClicked() instanceof Player) {
				Player p = (Player) e.getWhoClicked();
				if(inv.equals(p.getInventory())) return;
				if(PlotListInventory.invs.containsKey(p) && PlotListInventory.invs.containsValue(inv)) {
					Inventory i = PlotListInventory.invs.get(p);
					if(view.getTitle().endsWith("Plots")) {
						e.setCancelled(true);
						if(e.getCurrentItem() != null) {
							ItemStack item = e.getCurrentItem();
							if(item.getItemMeta().getDisplayName().endsWith("Schließen")) {
								p.closeInventory();
							} else {
								PlotID plotid = new PlotID();
								plotid.setID(item.getItemMeta().getDisplayName().replace("§b",""));
								PlotSystem.getInstance().getPlotManager().getPlotProfile(plotid).plotregion.teleportToPlot((Player)e.getWhoClicked());
							}
						}
					}
				}else if(PlotBuyInventory.invs.containsKey(p) && PlotBuyInventory.invs.containsValue(inv)) {
					Inventory i = PlotBuyInventory.invs.get(p);
					if(view.getTitle().endsWith("Plot kaufen")) {
						e.setCancelled(true);
						if(e.getCurrentItem() != null) {
							ItemStack item = e.getCurrentItem();
							if(item.getType() == Material.GREEN_STAINED_GLASS_PANE) {								
								PlotID plotid = new PlotID();
								plotid.setID(i.getItem(1).getItemMeta().getDisplayName().replace("§7PlotID: §f", ""));
								if(PlotSystem.getInstance().getPlotManager().isCorrectlyRegistered(plotid)) {									
									if(PlotSystem.getInstance().getPlotManager().givePlot(p, plotid)) {
										p.sendMessage("§aDu hast das Plot erfolgreich gekauft!");
										plotid.loadSignLocationFromFile();
										if(plotid.getLocation().getBlock().getState() instanceof Sign) {
											Sign sign = (Sign) plotid.getLocation().getBlock().getState();
											sign.setLine(0, "");
											sign.setLine(1, "§2Hier wohnt");
											sign.setLine(2, p.getName());
											sign.setLine(3, "");
											sign.update();
										}
										PlotSystem.getInstance().getPlotManager().plots.get(plotid.getID()).plotregion.teleportToPlot(p);
									}else {
										p.closeInventory();
										p.sendMessage("§cDu kannst das Plot nicht kaufen!");
									}
								}else p.sendMessage("§cDies ist kein käufliches Plot. Es handelt sich um ein fehlerhaftes Plot.");
							}else if(item.getType() == Material.RED_STAINED_GLASS_PANE) {								
								p.closeInventory();
							}
						}
					}					
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getInventory() != null) {
			//Hier werden die Inventare aus den Inventar Listen gelöscht, sobald die Inventar geschlossen werden
			if(PlotListInventory.invs.values().contains(e.getInventory())) {
				PlotListInventory.invs.remove(e.getPlayer(), e.getInventory());
				e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_CHEST_CLOSE, 0f, 0f);
			}else if(PlotBuyInventory.invs.values().contains(e.getInventory())) {
				PlotBuyInventory.invs.remove(e.getPlayer(), e.getInventory());
				e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_CHEST_CLOSE, 0f, 0f);
			}
		}
	}
	
	
}
