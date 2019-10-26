package me.teamdream.de.listener;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.teamdream.de.PlayerLocator;
import me.teamdream.de.PlotSystem;
import me.teamdream.de.inventory.PlotBuyInventory;
import me.teamdream.de.plotmanager.PlotManager;
import me.teamdream.de.plotmanager.PlotManager.PlotID;
import me.teamdream.de.plotmanager.PlotProfile;
import me.teamdream.de.plotmanager.PlotSession;

public class InteractListener implements Listener {
	private PlotManager plotmanager = null;
	
	public InteractListener() {
		plotmanager = PlotSystem.getInstance().getPlotManager();
		PlotSystem.getInstance().getServer().getPluginManager().registerEvents(this, PlotSystem.getInstance());
	}
	
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		/* Die Session wird registriert und Positionen werden gesetzt */
		if((e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) && p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE) {
			PlotSession session = plotmanager.getSession(p);
			if(session == null) plotmanager.registerSession(p);
			session = plotmanager.getSession(p);			
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				session.setPos2(e.getClickedBlock().getLocation());
				e.setCancelled(true);
				
			}else if(e.getAction() == Action.LEFT_CLICK_BLOCK && p.isSneaking()) {
				session.setSignPosition(e.getClickedBlock().getLocation());
				e.setCancelled(true);
			}else if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
				session.setPos1(e.getClickedBlock().getLocation());
				e.setCancelled(true);
			}
		}
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getPlayer() != null) {
				PlotProfile profile_player = null;
				PlotID plotid = null;
				if(e.getClickedBlock().getState() instanceof Sign) {
					Sign s = (Sign)e.getClickedBlock().getState();
					profile_player = PlotSystem.getCurrentPlot(e.getClickedBlock().getLocation());
					if(s.getLine(0).contains("ID:") && s.getLine(1).contains("Preis: ") && s.getLine(2).equals("Rechtsklick zum") && s.getLine(3).equals("Beanspruchen")) {
						plotid = new PlotID(s.getLocation());
						plotid.setID(s.getLine(0).replace("ID:", ""));
						if(PlotSystem.getInstance().getPlotManager().isCorrectlyRegistered(plotid)) {							
							profile_player = PlotSystem.getInstance().getPlotManager().getPlotProfile(plotid);
							profile_player.plotid = plotid;
							profile_player.preis = Integer.parseInt(s.getLine(1).replace("Preis: ", "").replace("€", ""));
							p.openInventory(new PlotBuyInventory().getInventory(p, profile_player));
							p.getWorld().playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 0f, 0f);
						}else p.sendMessage("§cDies ist kein käufliches Plot. Es handelt sich um ein fehlerhaftes Plot.");
					}
				}
				if(e.getClickedBlock().getType().isInteractable()) {					
					profile_player = PlotSystem.getCurrentPlot(p.getLocation());
					PlotProfile profile_block = PlotSystem.getCurrentPlot(e.getClickedBlock().getLocation());
					if(p.isOp() || p.hasPermission("plotmanager.modify.*")) return;
					if(profile_player != null && profile_block != null && profile_player.plotid.getID().equals(profile_block.plotid.getID())) {				
						if(profile_player.isMember(e.getPlayer().getUniqueId()) == false) e.setCancelled(true);
						else if(profile_player.getPlotPermissions(p.getUniqueId()).allow_interacting == false) e.setCancelled(true);
					}else e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		PlotProfile p = null;
		boolean wildnis = true;
		if(plotmanager.plots.isEmpty()) return;
		for(String s : plotmanager.plots.keySet()) {
			p = plotmanager.plots.get(s);
			if(p.plotregion != null && p.plotregion.isInRegion(e.getPlayer().getLocation())) {
				wildnis = false;
				break;
			}
		}
		if(plotmanager.locators.containsKey(e.getPlayer()) == false) plotmanager.locators.put(e.getPlayer(), new PlayerLocator(e.getPlayer()));
		if(wildnis) p = null;
		plotmanager.locators.get(e.getPlayer()).updatePlot(p);
	}
	
	public void playParticles(Location loc, Effect particle) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getWorld().getName().equals(loc.getWorld().getName())) {
				p.getWorld().playEffect(loc, particle, 3);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.getPlayer() != null) {
			if(e.getBlock().getState() instanceof Sign) {
				Sign s = (Sign)e.getBlock().getState();
				if(s.getLine(0).contains("ID:") && s.getLine(1).contains("Preis: ") && s.getLine(2).equals("Rechtsklick zum") && s.getLine(3).equals("Beanspruchen") && e.getPlayer().isSneaking() == false) e.setCancelled(true);
			}
			Player p = e.getPlayer();
			PlotProfile profile_block = PlotSystem.getCurrentPlot(e.getBlock().getLocation());
			if(p.isOp() || p.hasPermission("plotmanager.modify.*")) return;
			if(profile_block != null && profile_block.plotid != null) {
				if(plotmanager.getOwner(profile_block.plotid) != null && plotmanager.getOwner(profile_block.plotid).equals(p.getUniqueId())) e.setCancelled(false);
				else if(profile_block.isMember(e.getPlayer().getUniqueId()) == false) e.setCancelled(true);
				else if(profile_block.getPlotPermissions(p.getUniqueId()).allow_modify_plot == false) e.setCancelled(true);
			}else if(p.isOp() == false || p.hasPermission("plotmanager.modify.break") == false) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(e.getPlayer() != null) {
			Player p = e.getPlayer();
			PlotProfile profile_block = PlotSystem.getCurrentPlot(e.getBlockPlaced().getLocation());
			if(p.isOp() || p.hasPermission("plotmanager.modify.*")) return;
			if(profile_block != null && profile_block.plotid != null) {
				if(plotmanager.getOwner(profile_block.plotid) != null && plotmanager.getOwner(profile_block.plotid).equals(p.getUniqueId())) e.setCancelled(false);
				else if(profile_block.isMember(e.getPlayer().getUniqueId()) == false) e.setCancelled(true);
				else if(profile_block.getPlotPermissions(p.getUniqueId()).allow_modify_plot == false) e.setCancelled(true);
			}else if(p.isOp() == false || p.hasPermission("plotmanager.modify.break") == false) e.setCancelled(true);
		}
	}
	
}
