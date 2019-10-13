package me.teamdream.de;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import me.teamdream.de.commands.PlotCommands;
import me.teamdream.de.listener.InteractListener;
import me.teamdream.de.plotmanager.PlotManager;

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
		// TODO Auto-generated method stub
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
		
		getCommand("plot").setExecutor(new PlotCommands());
	}
	
	public PlotManager getPlotManager() {
		return this.plotmanager;
	}
	
}
