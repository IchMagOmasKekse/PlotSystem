package me.teamdream.de.plotmanager;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.teamdream.de.PlotSystem;
import me.teamdream.de.plotmanager.PlotManager.PlotID;
import me.teamdream.de.plotmanager.PlotManager.PlotRegion;

public class PlotProfile {
	
	public PlotID plotid = null;
	public CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();
	public PlotRegion plotregion = null;
	public ConcurrentHashMap<UUID, PlotPermission> members = new ConcurrentHashMap<UUID, PlotPermission>();
	public String displayname = "NO_DISPLAYNAME";
	public int preis = 0;
	
	public PlotProfile(PlotID plotid, PlotRegion plotregion, boolean loaddata) {
		this.plotid = plotid;
		this.plotregion = plotregion;
		if(loaddata) doSetup();
	}
	public PlotProfile(PlotID plotid, PlotRegion plotregion) {
		this.plotid = plotid;
		this.plotregion = plotregion;
		doSetup();
	}
	
	public void doSetup() {
		loadMembers();
		loadData();
	}
	
	
	public void loadMembers() {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(new File(PlotManager.home_path+"plot_list.yml"));
		ArrayList<String> mems = (ArrayList<String>) cfg.getStringList("Plots."+plotid.getID()+".Members");
		for(String s : mems) members.put(UUID.fromString(s), new PlotPermission(UUID.fromString(s)));
	}
	
	public void loadData(){
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(new File(PlotManager.home_path+"plot_list.yml"));
		
		displayname = cfg.getString("Plots."+plotid.getID()+".Displayname");
		preis = cfg.getInt("Plots."+plotid.getID()+".Preis");
		
	}
	
	private int plot_max_size = 300;
	
	int marker_height = 73;
	@SuppressWarnings("unchecked")
	public void filterRectangles() {
		ArrayList<String> temp_list = new ArrayList<String>();
		ArrayList<Block> temp_list2 = new ArrayList<Block>();
		Bukkit.getConsoleSender().sendMessage("Plot "+plotid+" wird in Rechtecke eingeteilt...");
		
		World world = null;
		double biggestx = -3000000d;
		double biggestz = -3000000d;
		double smallestx = 3000000d;
		double smallestz = 3000000d;
		CopyOnWriteArrayList <Block> blocks_copy = (CopyOnWriteArrayList <Block>) blocks.clone();
		
		int times = 1; //Sicherung damit die While-Schleife nach max_times Mal abbricht
		int max_times = 6;
		
		while((blocks_copy.size()) > 0 && times < max_times) {
			temp_list.clear();
			temp_list2.clear();
//			broadcast("blocks size: "+blocks_copy.size());
//			broadcast("Copy Size: "+blocks_copy.size()+"\nTimes: "+times+"/"+max_times+"\nBlocks_copy.size = "+(blocks_copy.size()));
			
			for(Block block : blocks_copy) {
				if(world == null) world = block.getWorld();
				if(block.getType().toString().contains("BRICK")) {
					if(block.getLocation().getX() > biggestx) biggestx = block.getLocation().getX();
					if(block.getLocation().getZ() > biggestz) biggestz = block.getLocation().getZ();
					if(block.getLocation().getX() < smallestx) smallestx = block.getLocation().getX();
					if(block.getLocation().getZ() < smallestz) smallestz = block.getLocation().getZ();
				}
			}
			
//			Bukkit.broadcastMessage("Filtering:\nBlocks Count: "+blocks.size()+"\nSmallestX: "+smallestx+"\nSmallestZ: "+smallestz+"\nBiggestX: "+biggestx+"\nBiggestZ: "+biggestz);
			Location loc1 = new Location(world, biggestx, 71, biggestz);
			Location loc2 = new Location(world, smallestx, 71, smallestz);
			loc1.getBlock().setType(Material.GLOWSTONE);
			loc2.getBlock().setType(Material.GLOWSTONE);
			
			double cx1 = -0d;
			double cz1 = -0d;
			
			double cx2 = -0d;
			double cz2 = -0d;
			
			double cx3 = -0d;
			double cz3 = -0d;
			
			double cx4 = -0d;
			double cz4 = -0d;
			
			for(Block b : blocks_copy) {
				if(b.getLocation().getZ() == smallestz && b.getType().toString().contains("BRICK")) {
					if(cx1 == -0d && cz1 == -0d) {
						cx1 = b.getLocation().getX();
						cz1 = b.getLocation().getZ();
						blocks_copy.remove(b);
					} else {
						cx2 = b.getLocation().getX();
						cz2 = b.getLocation().getZ();
						blocks_copy.remove(b);
					}
				}
			}
			/* Die Höhe 70 ist hardcoded! sie muss berechnet werden, damit das Grundstück erfasst wird! */
			Location lul = new Location(world, cx1, 70, cz1);
			
			for(int i = 0; i != plot_max_size; i++) {
				lul.add(0,0,1);
				if(lul.getBlock().getType().toString().contains("BRICK")) {
					cx3 = lul.getX();
					cz3 = lul.getZ();
					blocks_copy.remove(lul.getBlock());
				}else {
					lul.add(0,0,-1).getBlock().getType().toString().contains("BRICK");
					cx3 = lul.getX();
					cz3 = lul.getZ();
					break;
				}
			}
			cx4 = Double.valueOf(cx2);
			cz4 = Double.valueOf(cz3);
//			broadcast("§fErhalte Blöcke von §cX §f-> §cX§f: §c"+cx3+" §f-> §c"+cx4);
			for(int x = (int) cx3; x <= (int)cx4; x++) {
				temp_list.add(x+":"+(int)cz3);
			}
			
			for(Block b : blocks_copy) {
				if(b.getType().toString().contains("BRICK") && temp_list.contains(b.getLocation().getX()+":"+b.getLocation().getZ())) {
					temp_list2.add(b);
				}
			}
			for(Block b : temp_list2) {
				blocks_copy.remove(b);
			}
			
			Location ul = new Location(world, cx1, marker_height, cz1);
			ul.getBlock().setType(Material.LAPIS_BLOCK);
			new Location(world, cx2, marker_height, cz2).getBlock().setType(Material.DIAMOND_BLOCK);
			new Location(world, cx3, marker_height, cz3).getBlock().setType(Material.GOLD_BLOCK);
			Location br = new Location(world, cx4, marker_height, cz4);
			br.getBlock().setType(Material.IRON_BLOCK);

			blocks_copy = fill(ul.clone(),br.clone(), blocks_copy);
			marker_height++;
			times++;
			
			/* Reset */
			world = null;
			biggestx = -3000000d;
			biggestz = -3000000d;
			smallestx = 3000000d;
			smallestz = 3000000d;
			cx1 = -0d;
			cz1 = -0d;
			
			cx2 = -0d;
			cz2 = -0d;
			
			cx3 = -0d;
			cz3 = -0d;
			
			cx4 = -0d;
			cz4 = -0d;
		}
		marker_height = 73;
	}
	
	public ConcurrentHashMap<UUID, PlotPermission> getMembers() {
		return members;
	}
	
	public boolean addMember(UUID uuid) {
		return addMember(uuid, new PlotPermission(uuid));
	}
	
	public boolean addMember(UUID uuid, PlotPermission permission) {
		if(members.containsKey(uuid)) return false;
		members.put(uuid, permission);
		File file = new File(PlotManager.home_path+"plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		ArrayList<String> mems = (ArrayList<String>) cfg.getStringList("Plots."+plotid.getID()+".Members");
		if(mems.contains(uuid.toString()) == false) mems.add(uuid.toString());
		cfg.set("Plots."+plotid.getID()+".Members", mems);
		try {cfg.save(file);} catch (IOException e) {e.printStackTrace();}
		return true;
	}
	
	public boolean removeMember(UUID uuid) {
		if(members.containsKey(uuid) == false) return false;
		members.remove(uuid);
		File file = new File(PlotManager.home_path+"plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		ArrayList<String> mems = (ArrayList<String>) cfg.getStringList("Plots."+plotid.getID()+".Members");
		if(mems.contains(uuid.toString())) mems.remove(uuid.toString());
		
		cfg.set("Plots."+plotid.getID()+".Members", mems);
		cfg.set("Plots."+plotid.getID()+".Perms", members);
		try {cfg.save(file);} catch (IOException e) {e.printStackTrace();}
		return true;
	}
	
	public boolean isMember(UUID uuid) {
		return members.containsKey(uuid);
	}
	
	public PlotPermission getPlotPermissions(UUID uuid) {
		if(isMember(uuid)) return members.get(uuid);
		return null;
	}
	
	public boolean setPlotPermissions(UUID uuid, PlotPermission permission) {
		if(isMember(uuid)) members.replace(uuid, members.get(uuid), permission);
		else return false;
		return true;
	}
	
	public CopyOnWriteArrayList<Block> fill(Location ul, Location br, CopyOnWriteArrayList <Block> blocks_copy) {
		Location l = null;
		if(br.getBlockY()-ul.getBlockY() == 0) {
			for(int x = (int)ul.getBlockX(); x <= (int)br.getBlockX(); x++) {
				for(int z = (int)ul.getBlockZ(); z <= (int)br.getBlockZ(); z++) {
					l = new Location(ul.getWorld(),x,marker_height+1,z);
					l.getBlock().setType(Material.GRASS);
					l.setY(70);
					if(l.getBlock().getType().toString().contains("BRICK"))blocks_copy.remove(l.getBlock());
				}
			}
		}else {			
			for(int y = (int)ul.getBlockY(); y <= (int)br.getBlockY(); y++) {
				for(int x = (int)ul.getBlockX(); x <= (int)br.getBlockX(); x++) {
					for(int z = (int)ul.getBlockZ(); z <= (int)br.getBlockZ(); z++) {
						l = new Location(ul.getWorld(),x,marker_height+1,z);
						l.getBlock().setType(Material.GRASS);
						if(l.getBlock().getType().toString().contains("BRICK"))blocks_copy.remove(l.getBlock());
					}				
				}
			}
		}
		return blocks_copy;
	}
	
	
	
	public boolean broadcast(String msg) {
		Bukkit.broadcastMessage(msg);
		return true;
	}
	
	/* Fügt einen Block der Plot-Umrandung zur Liste hinzu */
	public boolean addBlock(Block block) {
		if(blocks.contains(block)) return false;
		else {
			blocks.add(block);
			return true;
		}
	}
	
	public boolean addSubRegion(Location pos1, Location pos2) {
		
		if(plotregion == null) {
			PlotSystem.getPlotManager();
			plotregion = PlotSystem.getPlotManager().getRegion(plotid);
			return plotregion.addLocation(pos1, pos2);
		}else return plotregion.addLocation(pos1, pos2);
	}
	
	public class PlotPermission {
		
		public UUID player_uuid = null;
		public boolean allow_modify_plot = true;
		public boolean allow_interacting = true;
		
		public PlotPermission(UUID uuid) {
			this.player_uuid = uuid;
		}
		
	}
	
	public String getDisplayname() {
		return this.displayname;
	}
	public boolean setDisplayname(String name) {
		this.displayname = name;
		return saveData();
	}
	public boolean setDisplayname(String name, boolean saveToFile) {
		this.displayname = name;
		if(saveToFile) return saveData();
		else return true;
	}
	
	public boolean saveData() {
		File file = new File(PlotManager.home_path+"plot_list.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		cfg.set("Plots."+plotid.getID()+".Displayname", getDisplayname());
		try {
			cfg.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
