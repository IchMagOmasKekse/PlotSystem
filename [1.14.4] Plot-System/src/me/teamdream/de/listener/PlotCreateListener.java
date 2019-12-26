package me.teamdream.de.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.teamdream.de.PlotSystem;
import me.teamdream.de.event.PlotCreateEvent;

public class PlotCreateListener implements Listener {
	
	public PlotCreateListener() {
		PlotSystem.getInstance().getServer().getPluginManager().registerEvents(this, PlotSystem.getInstance());
	}
	
	@EventHandler
	public void onCreate(PlotCreateEvent e) {
		Bukkit.broadcastMessage("PlotCreate");
	}
	
}
