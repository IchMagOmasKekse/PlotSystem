package me.teamdream.de;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import me.teamdream.de.commands.PlotCommands;
import me.teamdream.de.commands.Plots;
import me.teamdream.de.listener.InteractListener;
import me.teamdream.de.listener.InventoryListener;
import me.teamdream.de.plotmanager.PlotManager;
import me.teamdream.de.plotmanager.PlotProfile;

public class PlotSystem extends JavaPlugin {
	
	private static PlotSystem pl = null;
	public static PlotSystem getInstance() {
		return pl;
	}
	private PlotManager plotmanager = null;
	
	public PlotSystem() { }
	
	@Override
	public void onEnable() {
		
		preInit();
		init();
		postInit();
		
		super.onEnable();
	}
	@Override
	public void onDisable() {
		plotmanager.closeSessions();
		super.onDisable();
	}
	
	public void preInit() {
		pl = this;
		
		if(new File("plugins/PlotSystem/plot_list.yml").exists() == false) saveResource("plot_list.yml", true);
	}
	public void init() {
		
	}
	public void postInit() {
		plotmanager = new PlotManager();
		
		new InteractListener();
		new InventoryListener();
		
		getCommand("plot").setExecutor(new PlotCommands());
		getCommand("plots").setExecutor(new Plots());
	}
	
	public PlotManager getPlotManager() {
		return this.plotmanager;
	}
	
	public static PlotProfile getCurrentPlot(Location loc) {
		PlotProfile p = null;
		boolean wildnis = true;
		for(String s : PlotSystem.getInstance().getPlotManager().plots.keySet()) {
			p = PlotSystem.getInstance().getPlotManager().plots.get(s);
			if(p.plotregion != null && p.plotregion.isInRegion(loc)) {
				wildnis = false;
				break;
			}
		}
		if(wildnis) return null;
		return p;
	}
	
}
