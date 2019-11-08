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

import me.teamdream.de.PlayerProfiler;
import me.teamdream.de.PlotSystem;
import me.teamdream.de.SystemSettings;
import me.teamdream.de.inventory.PlotBuyInventory;
import me.teamdream.de.inventory.PlotListInventory;
import me.teamdream.de.plotmanager.PlotManager;
import me.teamdream.de.plotmanager.PlotManager.PlotID;
import me.teamdream.de.server.AccountManager;

public class InventoryListener implements Listener {
	
	private PlotManager pmanager = PlotSystem.getPlotManager();
	
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
								pmanager.getPlotProfile(plotid).plotregion.teleportToPlot((Player)e.getWhoClicked());
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
								if(pmanager.isCorrectlyRegistered(plotid)) {
									//TODO: Abfragen, ob der Spieler genug Geld hat & ob der Spieler die maximale Anzahl an Plots erreicht hat
									if(PlayerProfiler.howManyPlotsHas(p) < SystemSettings.maxPlotsPerPlayer) {										
										if(AccountManager.getMoney(p.getUniqueId()) >= pmanager.plots.get(plotid.getID()).preis) {
											if(AccountManager.sendMoney(p.getUniqueId(), null, pmanager.plots.get(plotid.getID()).preis)) {	
												p.sendMessage("§c§o-"+pmanager.getPlotProfile(plotid).preis+"€");
												if(pmanager.givePlot(p, plotid)) {
													PlayerProfiler.addBoughtPlot(p, 1);
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
													pmanager.plots.get(plotid.getID()).plotregion.teleportToPlot(p);
												}else {
													p.closeInventory();
													p.sendMessage("§cDu kannst das Plot nicht kaufen!");
												}
											}else p.sendMessage("§cEtwas ist beim Transfer zwischen dir und dem Server-Konto schief gelaufen...");
										}else p.sendMessage("§cDu kannst dir dieses Grundstück nicht leisten!");
									}else p.sendMessage("§cDu hast die maximale Anzahl an Plots erreicht!("+SystemSettings.maxPlotsPerPlayer+")");
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
