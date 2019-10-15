package me.teamdream.de.plotmanager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlotManager {
	
	private static String home_path = "plugins/PlotSystem/";
	
	public HashMap<String, PlotProfile> plots = new HashMap<String, PlotProfile>();
	
	public PlotManager() {
		
	}
	
	public boolean givePlot(Player p, PlotID plotid) {
		File file = new File(home_path+"plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		if(isAvailable(plotid)) {
			cfg.set("Plots."+plotid.getID()+".Is Available", false);
			cfg.set("Plots."+plotid.getID()+".Owner", p.getUniqueId());
			try { cfg.save(file); return true; } catch (IOException e) { e.printStackTrace(); return false; }
		}else return false;
	}
	
	public boolean registerPlot(PlotID plotid) {
		if(plots.containsKey(plotid.getID()) == false) plots.put(plotid.getID(), new PlotProfile(plotid.getID()));
		if(existPlot(plotid) == false) {
			File file = new File(home_path+"plot_list.yml");
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			cfg.set("Plots."+plotid.getID()+".Is Available", true);
			cfg.set("Plots."+plotid.getID()+".World", plotid.getLocation().getWorld().getName());
			cfg.set("Plots."+plotid.getID()+".Sign X", plotid.getLocationX());
			cfg.set("Plots."+plotid.getID()+".Sign Y", plotid.getLocationY());
			cfg.set("Plots."+plotid.getID()+".Sign Z", plotid.getLocationZ());
			
			try { cfg.save(file); return true; } catch (IOException e) { e.printStackTrace(); return false; }
		}
		
		return true;
	}
	
	public boolean isAvailable(PlotID plotid) {
		File file = new File(home_path+"plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		return cfg.getBoolean("Plots."+plotid.getID()+".Is Available");
	}
	
	public boolean existPlot(PlotID plotid) {
		File file = new File(home_path+"plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		String key = "Plots."+plotid.getID()+".World";
		if(cfg.getString(key) == null) return false;
		else return true;
	}
	
	
	private Random random = new Random();
	public String generatePlotID() {
		String id = "";
		id = id + random.nextInt(999999);
		return id;
	}
	
	public PlotProfile getPlotProfile(PlotID plotid) {
		return plots.get(plotid.getID());
	}
	
	public static class PlotID {
		
		private Location loc = null;
		private String id = "KEINE ID FESTGELEGT";
		
		public PlotID(Location loc) {
			this.loc = loc;
		}
		
		public PlotID() {}
		
		@SuppressWarnings("unused")
		public String getID() {
			if(id.equals("KEINE ID FESTGELEGT")) {
				File file = new File(home_path+"plot_list.yml");
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				String key = "Plots."+loc.getWorld().getName()+"."+loc.getX()+"."+loc.getY()+"."+loc.getZ();				
//				return cfg.getString(key);
				return "MUSS PROGRAMMIERT WERDEN";
			}else return id;
		}
		
		public void setID(String id) {
			this.id = id;
		}
		
		public Location getLocation() {
			return loc;
		}
		
		public int getLocationX() {
			return (int) loc.getX();
		}
		public int getLocationY() {
			return (int) loc.getY();
		}
		public int getLocationZ() {
			return (int) loc.getZ();
		}
		
	}
	
}
