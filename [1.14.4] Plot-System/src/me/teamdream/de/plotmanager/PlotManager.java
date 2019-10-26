package me.teamdream.de.plotmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.teamdream.de.Cuboid;
import me.teamdream.de.PlayerLocator;

public class PlotManager {
	
	public static String home_path = "plugins/PlotSystem/";
	
	public HashMap<String, PlotProfile> plots = new HashMap<String, PlotProfile>();
	public HashMap<Player, PlotSession> sessions = new HashMap<Player, PlotSession>();
	public HashMap<Player, PlayerLocator> locators = new HashMap<Player, PlayerLocator>();
	
	public PlotManager() {
		loadPlots();
	}
	
	public void loadPlots() {
		plots.clear();
		File file = new File(home_path+"/plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		ArrayList<String> names = (ArrayList<String>) cfg.getStringList("Plotnames");
		PlotID plotid = null;
		PlotRegion region = null;
		for(String n : names) {
			plotid = new PlotID();
			plotid.setID(n);
			plotid.loadSignLocationFromFile();
			plots.put(n, new PlotProfile(plotid, region));
			region = getRegion(plotid);
			plots.get(n).plotregion = region;
		}
	}
	
	public boolean givePlot(Player p, PlotID plotid) {
		File file = new File(home_path+"/plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		if(plots.containsKey(plotid.getID())) {
			if(isCorrectlyRegistered(plotid) == false) return false;
			if(getOwner(plotid) == null) {
				cfg.set("Plots."+plotid.getID()+".Owner", p.getUniqueId().toString());
				try { cfg.save(file); return true; } catch (IOException e) { e.printStackTrace(); return false; }
			}else return false;
		}else return false;
	}
	
	public boolean isCorrectlyRegistered(PlotID plotid) {
		File f = new File(home_path+"/plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		return cfg.getBoolean("Plots."+plotid.getID()+".isValid");
	}
	
	public boolean registerPlot(PlotID plotid, PlotSession session) {
		if(existPlot(plotid) == false) {
			File file = new File(home_path+"/plot_list.yml");
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			ArrayList<String> names = (ArrayList<String>) cfg.getStringList("Plotnames");
			names.add(plotid.getID());
			cfg.set("Plotnames", names);
			cfg.set("Plots."+plotid.getID()+".isValid", true);
			cfg.set("Plots."+plotid.getID()+".Owner", "none");
			cfg.set("Plots."+plotid.getID()+".Subregions", 0);
			cfg.set("Plots."+plotid.getID()+".Members", new ArrayList<String>());
			cfg.set("Plots."+plotid.getID()+".World", plotid.getLocation().getWorld().getName());
			cfg.set("Plots."+plotid.getID()+".Sign X", plotid.getSignLocationX());
			cfg.set("Plots."+plotid.getID()+".Sign Y", plotid.getSignLocationY());
			cfg.set("Plots."+plotid.getID()+".Sign Z", plotid.getSignLocationZ());
			cfg.set("Plots."+plotid.getID()+".X1", session.getPos1().getX());
			cfg.set("Plots."+plotid.getID()+".Y1", 256);
			cfg.set("Plots."+plotid.getID()+".Z1", session.getPos1().getZ());
			cfg.set("Plots."+plotid.getID()+".X2", session.getPos2().getX()+1);
			cfg.set("Plots."+plotid.getID()+".Y2", 0);
			cfg.set("Plots."+plotid.getID()+".Z2", session.getPos2().getZ()+1);
			
			try {
				cfg.save(file); 
				PlotProfile profile = new PlotProfile(plotid, getRegion(plotid));
				if(plots.containsKey(plotid.getID()) == false) {
					plots.put(plotid.getID(), profile);
				}
				loadPlots();
				return true; } catch (IOException e) { e.printStackTrace(); return false; }
		}
		
		return true;
	}
	
	public PlotRegion getRegion(PlotID plotid) {
		if(existPlot(plotid) == true) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(new File(PlotManager.home_path+"plot_list.yml"));
			int subregions = cfg.getInt("Plots."+plotid.getID()+".Subregions");
			PlotRegion plotregion = new PlotRegion(plotid);
			String world = cfg.getString("Plots."+plotid.getID()+".World");
			double x = cfg.getDouble("Plots."+plotid.getID()+".X1");
			double y = cfg.getDouble("Plots."+plotid.getID()+".Y1");
			double z = cfg.getDouble("Plots."+plotid.getID()+".Z1");
			double x2 = cfg.getDouble("Plots."+plotid.getID()+".X2");
			double y2 = cfg.getDouble("Plots."+plotid.getID()+".Y2");
			double z2 = cfg.getDouble("Plots."+plotid.getID()+".Z2");
			plotregion.putLocationInList(new Location(Bukkit.getWorld(world), x, y, z), new Location(Bukkit.getWorld(world), x2, y2, z2));
			if(subregions > 0) {				
				for(int i = 1; i < subregions+1; i++) {
					Bukkit.broadcastMessage("Adding Sub: "+i);
					x = cfg.getDouble("Plots."+plotid.getID()+".Sub"+i+".X1");
					y = cfg.getDouble("Plots."+plotid.getID()+".Sub"+i+".Y1");
					z = cfg.getDouble("Plots."+plotid.getID()+".Sub"+i+".Z1");
					x2 = cfg.getDouble("Plots."+plotid.getID()+".Sub"+i+".X2");
					y2 = cfg.getDouble("Plots."+plotid.getID()+".Sub"+i+".Y2");
					z2 = cfg.getDouble("Plots."+plotid.getID()+".Sub"+i+".Z2");
					plotregion.putLocationInList(new Location(Bukkit.getWorld(world), x, y, z), new Location(Bukkit.getWorld(world), x2, y2, z2));
				}
			}
			return plotregion;
		}else{
			return null;	
		}
	}
	
	public UUID getOwner(PlotID plotid) {
		File file = new File(home_path+"/plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		try{
			String s_uuid = cfg.getString("Plots."+plotid.getID()+".Owner");
			if(s_uuid.equals("none") == false) {
				UUID uuid = UUID.fromString(s_uuid);
				return uuid;	
			} else return null;
		}catch(NullPointerException ex) {
			return null;
		}catch(IllegalArgumentException ex2) {
			return null;			
		}
	}
	
	public boolean existPlot(PlotID plotid) {
		return plots.containsKey(plotid.getID());
	}
	
	
	private Random random = new Random();
	public String generatePlotID() {
		String id = "";
		id = id + random.nextInt(999999);
		return id;
	}
	
	public PlotProfile getPlotProfile(PlotID plotid) {
		if(plots.containsKey(plotid.getID())) return plots.get(plotid.getID());
		else {
			plots.put(plotid.getID(), new PlotProfile(plotid, getRegion(plotid)));
			return plots.get(plotid.getID());
		}
	}
	
	public PlotSession registerSession(Player p) {
		if(sessions.containsKey(p)) return sessions.get(p);
		else {
			sessions.put(p, new PlotSession(p));
			return sessions.get(p);
		}
	}
	
	public boolean removeSession(Player p) {
		if(sessions.containsKey(p)) {
			sessions.remove(p);
			return true;
		}else return false;
	}
	
	public boolean knowsSession(Player p) {
		return sessions.containsKey(p);
	}
	
	public PlotSession getSession(Player p) {
		if(knowsSession(p)) return sessions.get(p);
		else return null;
	}
	
	public void closeSessions() {
		for(PlotSession s : sessions.values()) s.closeSession();
		for(PlayerLocator s : locators.values()) s.close();
	}
	
	public static class PlotID {
		
		private Location loc = null;
		private String id = "KEINE ID FESTGELEGT";
		
		public PlotID(Location loc) {
			this.loc = loc;
		}
		
		public PlotID() {}
		
		public void loadSignLocationFromFile() {
			File file = new File(home_path+"/plot_list.yml");
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			
			String world = cfg.getString("Plots."+getID()+".World");
			double x = cfg.getDouble("Plots."+getID()+".Sign X");
			double y = cfg.getDouble("Plots."+getID()+".Sign Y");
			double z = cfg.getDouble("Plots."+getID()+".Sign Z");
			this.loc = new Location(Bukkit.getWorld(world), x, y, z);
		}
		
		public String getID() {
			return id;
		}
		
		public void setID(String id) {
			this.id = id;
		}
		
		public Location getLocation() {
			return loc;
		}
		
		public int getSignLocationX() {
			return (int) loc.getX();
		}
		public int getSignLocationY() {
			return (int) loc.getY();
		}
		public int getSignLocationZ() {
			return (int) loc.getZ();
		}
		
	}
	
	public class PlotRegion {
		
		private PlotID plotid = null;
		ArrayList<Cuboid> regions = new ArrayList<Cuboid>();
		
		public PlotRegion(PlotID plotid) {
			this.plotid = plotid;
		}
		
		public boolean putLocationInList(Location pos1, Location pos2) {
			boolean contains = false;
			for(Cuboid cuboid : regions) {
				if(((int)cuboid.getLowerNE().getBlockX()) == ((int)pos1.getBlockX())) {
					if(((int)cuboid.getLowerNE().getBlockZ()) == ((int)pos1.getBlockZ())) {
						if(((int)cuboid.getUpperSW().getBlockX()) == ((int)pos2.getBlockX())) {
							if(((int)cuboid.getUpperSW().getBlockZ()) == ((int)pos2.getBlockZ())) {
								contains = true;
							}
						}
					}
				}
			}
			if(contains == false) {
				regions.add(new Cuboid(pos1.clone(), pos2.clone()));
			}
			return true;
		}
		
		public boolean addLocation(Location pos1, Location pos2) {
			boolean contains = false;
			for(Cuboid cuboid : regions) {
				if(((int)cuboid.getLowerNE().getBlockX()) == ((int)pos1.getBlockX())) {
					if(((int)cuboid.getLowerNE().getBlockZ()) == ((int)pos1.getBlockZ())) {
						if(((int)cuboid.getUpperSW().getBlockX()) == ((int)pos2.getBlockX())) {
							if(((int)cuboid.getUpperSW().getBlockZ()) == ((int)pos2.getBlockZ())) {
								contains = true;
							}
						}
					}
				}
			}
			if(contains == false) {
				File file = new File(home_path+"/plot_list.yml");
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				int subs = cfg.getInt("Plots."+plotid.getID()+".Subregions");
				subs++;
				pos1.setY(256);
				pos2.setY(0);
				pos2.add(1,0,1);
				cfg.set("Plots."+plotid.getID()+".Subregions", (subs));
				cfg.set("Plots."+plotid.getID()+".Sub"+subs+".X1", pos1.getX());
				cfg.set("Plots."+plotid.getID()+".Sub"+subs+".Y1", pos1.getY());
				cfg.set("Plots."+plotid.getID()+".Sub"+subs+".Z1", pos1.getZ());
				cfg.set("Plots."+plotid.getID()+".Sub"+subs+".X2", pos2.getX());
				cfg.set("Plots."+plotid.getID()+".Sub"+subs+".Y2", pos2.getY());
				cfg.set("Plots."+plotid.getID()+".Sub"+subs+".Z2", pos2.getZ());
				regions.add(new Cuboid(pos1.clone(), pos2.clone()));
				try {cfg.save(file);} catch (IOException e) {e.printStackTrace();}
			}
			return true;
		}
		
		public boolean isInRegion(Location loc) {
			if(regions.isEmpty() == false) {
				for(Cuboid cuboid : regions) if(cuboid.contains(loc)) return true;
//				Location l2 = null;
//				for(Location l : locations.keySet()) {
//					l2 = locations.get(l).clone();
					
					
//					if(loc.getBlockX() >= l.getBlockX() && loc.getBlockX() <= l2.getBlockX() && loc.getBlockZ() >= l.getBlockZ() && loc.getBlockZ() <= l2.getBlockZ()) return true;	
//					if(loc.getBlockX() >= l2.getBlockX() && loc.getBlockX() <= l.getBlockX() && loc.getBlockZ() >= l2.getBlockZ() && loc.getBlockZ() <= l.getBlockZ()) return true;	
//					if(((loc.getX() >= l.getX() && loc.getX() <= l2.getX()) || (loc.getX() >= l2.getX() && loc.getX() <= l.getX())) && 
//							((loc.getY() >= l.getY() && loc.getY() <= l2.getY()) || (loc.getY() >= l2.getY() && loc.getY() <= l.getY())) && 
//							((loc.getZ() >= l.getZ() && loc.getZ() <= l2.getZ()) || (loc.getZ() >= l2.getZ() && loc.getZ() <= l.getZ()))) {
//						return true;
//					}else return false;
//				}
			}
			return false;
		}

		public void teleportToPlot(Player player) {
			if(regions.isEmpty() == false) {
				for(Cuboid cuboid : regions) {
					player.teleport(cuboid.getCenter().getWorld().getHighestBlockAt(cuboid.getCenter()).getLocation());
					break;
				}
			}
		}
	}
}
