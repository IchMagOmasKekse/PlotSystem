
package me.teamdream.de.schematic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

import me.teamdream.de.PlotSystem;

public class SchematicManager {
	
	@SuppressWarnings("unused")
	private PlotSystem plotSystem = PlotSystem.getInstance();
	
	public SchematicManager() {
		
	}
	
	public void enable() {
		//TODO: Bisher keinen Nutzen
	}
	
	public boolean findSchematic(String path) {
		//TODO: Bisher keinen Nutzen
		return false;
	}
	
	public boolean saveSchematic(Schematic schem) {
		return saveSchematic(schem, false);
	}
	
	//
	//Schematic:
	//  Blocks:
	//    3:2:12:
	//      Type: DIRT
	//      Data: 0
	//      BlockData: null
	//      Powered: false
	//    4:2:12:
	//      Type: STONE
	//      Data: 2
	//      BlockData: null
	//      Powered: false
	public boolean saveSchematic(Schematic schem, boolean overwrite) {
		int index = 0;
		boolean skip = false;
		String offsetString = "";
		File file = new File(schem.path);
		if(overwrite == false && file.exists()) return false;
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		if(schem.blocks.isEmpty() == false) {			
			for(Block b : schem.blocks.values()) {
				if(schem.save_air == false && b.getType() == Material.AIR) skip = true;
				offsetString = schem.getLocationOffsetString((Block)schem.blocks.values().toArray()[index]);
//			offsetString = schem.getLocationOffsetString(b);
				if(skip != true) {
					cfg.set("Schematic.Blocks."+offsetString+".Type", b.getType().toString());
//					cfg.set("Schematic.Blocks."+offsetString+".Data", b.getType().getData().toGenericString());
//					cfg.set("Schematic.Blocks."+offsetString+".BlockData", b.getBlockData());
					cfg.set("Schematic.Blocks."+offsetString+".Powered", b.getBlockPower());
				}
				index++;
			}
		}
		try {cfg.save(file);Bukkit.getConsoleSender().sendMessage("§2Schematic gespeichert!");return true; } catch (IOException e) {e.printStackTrace();Bukkit.getConsoleSender().sendMessage("§4Schematic wurde nicht gespeichert!"); return false;}
	}
	
	public Schematic getSchematic() {
		return null;
	}
	
	public class Schematic {
		
		public String path = "";
		public Location pos1 = null;
		public Location pos2 = null;
		public HashMap<Location, Block> blocks = new HashMap<Location, Block>();
		public HashMap<Location, Entity> entities = new HashMap<Location, Entity>();
		public boolean place_air = true;
		public boolean save_air = true;
		public boolean load_air = true;
		
		public Schematic(String path, Location pos1, Location pos2) {
			this.path = path;
			this.pos1 = pos1;
			this.pos2 = pos2;
			loadSchematicFromFile();
			scanBlocks();
		}
		public Schematic(String path, Location pos1, Location pos2, boolean loadSchematic) {
			this.path = path;
			this.pos1 = pos1;
			this.pos2 = pos2;
			if(loadSchematic) loadSchematicFromFile();
			scanBlocks();
		}
		public boolean scanBlocks() {
			if(pos1.getWorld() != pos2.getWorld() || pos1 == null || pos2 == null) return false;
			Location loc;
			for(int y = (int)pos1.getY(); y < (int)pos2.getY(); y++) {
				for(int x = (int)pos1.getX(); x < (int)pos2.getX(); x++) {
					for(int z = (int)pos1.getZ(); z < (int)pos2.getZ(); z++) {
						loc = new Location(pos1.getWorld(),x,y,z);
						blocks.put(loc, loc.getBlock());
					}
				}
			}
			return true;
		}
		public boolean loadSchematicFromFile() {
			//TODO: muss gemacht werden
			return false;
		}
		public boolean addToSchematic(Schematic targetSchematic) {
			//TODO: muss gemacht werden
			return false;
		}
		
		public void setPath(String path) {
			 this.path = path;
		}
		public void clearLists() {
			blocks.clear();
			entities.clear();
		}
		public String getLocationOffsetString(Block b) {
			String offset = "";
			offset += (pos1.getX()-b.getLocation().getX())+":";
			offset += (pos1.getY()-b.getLocation().getY())+":";
			offset += (pos1.getZ()-b.getLocation().getZ())+":";
			System.out.println("Offset: "+offset);
			return offset;
		}
		
	}
	
}
