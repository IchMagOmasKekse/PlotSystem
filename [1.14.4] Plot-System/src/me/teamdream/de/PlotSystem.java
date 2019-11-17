package me.teamdream.de;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
	public boolean isSecure = false;
	private PlotManager plotmanager = null;
	public ArrayList<String> server_whitelist = new ArrayList<String>();
	
	public PlotSystem() {
		server_whitelist.add("25.79.112.93:25565");
		server_whitelist.add("localhost:25565");
		server_whitelist.add("localhost");
		server_whitelist.add("localhost:localhost");
		server_whitelist.add(":25565");
		server_whitelist.add(":25585");
		server_whitelist.add(":25515");
	}
	
	@Override
	public void onEnable() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getName().equals("IchMagOmasKekse")) {
				p.sendMessage("§7PlotSystem Infos:");
				p.sendMessage("    §7Server-IP = "+getServer().getIp());
				p.sendMessage("    §7Server-Port = "+getServer().getPort()); // == Port vom Server
			}
		}
		getServer().getConsoleSender().sendMessage("IP = "+getServer().getIp());
		getServer().getConsoleSender().sendMessage("Name = "+getServer().getName());
		getServer().getConsoleSender().sendMessage("Port = "+getServer().getPort());
		if(secureBoot() == false) {
			sendInsecureMessage(4, true);
		}else {
			getServer().getConsoleSender().sendMessage("§eDieser Server ist verifiziert.");
			getServer().getConsoleSender().sendMessage("§eDas Plugin wurde nun aktiviert!");
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.getName().equals("IchMagOmasKekse")) {
					p.sendMessage("§eDer Server ist verifizert.");
					p.sendMessage("§eDas Plugin wurde nun aktiviert!");
				}
			}
		}
	}
	@Override
	public void onDisable() {
		if(plotmanager != null) plotmanager.closeSessions();
	}
	
	public boolean secureBoot() {
		if(server_whitelist.contains(getServer().getIp().toString()+":"+getServer().getPort())) {
			isSecure = true; //Wenn isSecure nicht TRUE ist, kann das Plugin nicht starten und es gibt Fehler
			preInit();
			init();
			postInit();
			return true;
		}else return false;
			
	}
	
	public void preInit() {
		if(isSecure) {			
			pl = this;
			
			if(new File("plugins/PlotSystem/plot_list.yml").exists() == false) saveResource("plot_list.yml", true);
			if(new File("plugins/PlotSystem/settings.yml").exists() == false) saveResource("settings.yml", true);
		}else sendInsecureMessage(0, true);
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
	
	public static PlotManager getPlotManager() {
		return getInstance().plotmanager;
	}
	
	public static PlotProfile getCurrentPlot(Location loc) {
		PlotProfile p = null;
		boolean wildnis = true;
		for(String s : PlotSystem.getPlotManager().plots.keySet()) {
			p = PlotSystem.getPlotManager().plots.get(s);
			if(p.plotregion != null && p.plotregion.isInRegion(loc)) {
				wildnis = false;
				break;
			}
		}
		if(wildnis) return null;
		return p;
	}
	
	private boolean sendErrorHeaderMessage = true;
	private boolean sendInvalidMessage = true;
	public void sendInsecureMessage(int insecureIndex, boolean stopPluginFunctionality) {
		if(sendErrorHeaderMessage) {
			Bukkit.getConsoleSender().sendMessage("§cUnbefugtes Benutzen des Plugins.");
			Bukkit.getConsoleSender().sendMessage("§cCode "+insecureIndex+":");
		} else Bukkit.getConsoleSender().sendMessage("§cCode "+insecureIndex+":");
		switch(insecureIndex) {
		case 0:
			Bukkit.getConsoleSender().sendMessage("   §cEs wurde versucht das Plugin im gefährdetem Zustand zu starten.");
			sendErrorHeaderMessage = false;
			if(stopPluginFunctionality) this.isSecure = false;
			break;
		case 4:
			Bukkit.getConsoleSender().sendMessage("   §cDieses Plugin kann und darf nicht auf diesem Server ausgeführt werden.");
			sendErrorHeaderMessage = false;
			if(stopPluginFunctionality) this.isSecure = false;
			break;
		}
		if(isSecure == false) {
			Bukkit.shutdown();
			if(sendInvalidMessage) {
				getServer().getConsoleSender().sendMessage("");
				getServer().getConsoleSender().sendMessage("§c-> Der Server ist nicht verifiziert.");
				for(Player p : Bukkit.getOnlinePlayers()) p.sendMessage("§c-> Der Server ist nicht verifiziert.");
			}
			sendInvalidMessage = false;
		}
	}
	
}
