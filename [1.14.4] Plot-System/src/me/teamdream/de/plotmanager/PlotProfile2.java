package me.teamdream.de.plotmanager;


import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class PlotProfile2 {
	
	public String plotname = "Unnamed";
	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<Block> blocks_red = new ArrayList<Block>();
	
	public PlotProfile2(String plotname) {
		this.plotname = plotname;
	}
	
	private int plot_max_size = 300;
	
	@SuppressWarnings("unchecked")
	public void filterRectangles() {
		blocks_red.clear();
		Bukkit.getConsoleSender().sendMessage("Plot "+plotname+" wird in Rechtecke eingeteilt...");
		
		
//		double ax =  3000000d;
//		double az =  3000000d;
//		double bx = -3000000d;
//		double bz = -3000000d;
		double ax = -0d;
		double az = -0d;
		
		World world = null;
		double biggestx = -3000000d;
		double biggestz = -3000000d;
		double smallestx = 3000000d;
		double smallestz = 3000000d;
		ArrayList<Block> blocks_copy = (ArrayList<Block>) blocks.clone();
		for(Block block : blocks_copy) {
			if(world == null) world = block.getWorld();
			if(block != null && block.getType() == Material.BRICKS) {
				if(block.getLocation().getX() > biggestx && block.getLocation().getZ() > biggestz) {
					biggestx = block.getLocation().getX();
					biggestz = block.getLocation().getZ();
				}
				if(block.getLocation().getX() < smallestx && block.getLocation().getZ() < smallestz) {
					smallestx = block.getLocation().getX();
					smallestz = block.getLocation().getZ();
				}
				
//				if(block.getLocation().getX() > biggestx) biggestx = block.getLocation().getX();
//				if(block.getLocation().getX() < smallestx) smallestx = block.getLocation().getX();
//				if(block.getLocation().getZ() > biggestz) biggestz = block.getLocation().getZ();
//				if(block.getLocation().getZ() < smallestz) smallestz = block.getLocation().getZ();
			}
		}
		Bukkit.broadcastMessage("Filtering:\nBlocks Count: "+blocks.size()+"\nSmallestX: "+smallestx+"\nSmallestZ: "+smallestz+"\nBiggestX: "+biggestx+"\nBiggestZ: "+biggestz);
		Location loc1 = new Location(world, biggestx, 71, biggestz);
		Location loc2 = new Location(world, smallestx, 71, smallestz);
		Location loc = null;
		if(loc1.getWorld() == null) broadcast("Loc1world == null");
		if(loc2.getWorld() == null) broadcast("Loc2world == null");
		if(loc1 == null) broadcast("Loc1 == null");
		if(loc2 == null) broadcast("Loc2 == null");
		loc1.getBlock().setType(Material.GLOWSTONE);
		loc2.getBlock().setType(Material.GLOWSTONE);
		
		double subx1 = -0d;
		double subz1 = -0d;
		double subx2 = -0d;
		double subz2 = -0d;
		
		int times = 1; //Sicherung damit die While-Schleife nach max_times Mal abbricht
		int max_times = 100;
		
		
//		while((blocks_copy.size()-blocks_red.size()) > 0 && times < max_times) {
		
		
//		FUnktioniert bisher am besten
		
		
//			broadcast("Copy Size: "+blocks_copy.size()+"\nTimes: "+times+"/"+max_times);
//			for(int iz = 0; iz != blocks_copy.size(); iz++) {					
//				for(int ix = 0; ix != blocks_copy.size(); ix++) {
//					for(Block b : blocks_copy) {
//						loc = b.getLocation().clone();
//						if(b.getType().toString().contains("BRICK")) {
//							if(ax == -0d) {
//								ax = b.getLocation().getX();
//								subx1 = ax;
//							}
//							if(az == -0d) {
//								az = b.getLocation().getZ();
//								subz1 = az;
//							}
//							b.setType(Material.REDSTONE_BLOCK);
//							if((int)b.getLocation().getX() >= (int)(ax) && (int)b.getLocation().getZ() >= (int)(az)) {							
//								broadcast((int)b.getLocation().getX()+" == "+(int)(ax)+" && "+(int)b.getLocation().getZ()+" == "+(int)(az+iz));
//								subx2 = b.getLocation().getX();
////								subz2 = b.getLocation().getZ();
//								loc.add(0,3,0).getBlock().setType(Material.BEDROCK);
//							
//								blocks_red.add(b);
//							}
//							if((int)b.getLocation().getZ() >= (int)(az) && (int)b.getLocation().getX() >= (int)(ax)) {							
//								broadcast((int)b.getLocation().getX()+" == "+(int)(ax)+" && "+(int)b.getLocation().getZ()+" == "+(int)(az+iz));
////								subx2 = b.getLocation().getX();
//								subz2 = b.getLocation().getZ();
//								loc.add(0,3,0).getBlock().setType(Material.BEDROCK);
//								
//								blocks_red.add(b);
//							}
//						}
//					}
//				}
//			}
//			times++;
//		}
//		broadcast("subx1/subz1: "+subx1+"/"+subz1+" subx2/subz2: "+subx2+"/"+subz2);
		
		while((blocks_copy.size()-blocks_red.size()) > 0 && times < max_times) {
//			broadcast("Copy Size: "+blocks_copy.size()+"\nTimes: "+times+"/"+max_times);
			for(Block b : blocks_copy) {
				if(b.getType().toString().contains("BRICK")) {
					if(subx1 == -0d) subx1 = b.getLocation().getX();
					if(subz1 == -0d) subz1 = b.getLocation().getZ();
					if(b.getLocation().getX() < subx1 && b.getLocation().getZ() < subz1) {
						subx1 = b.getLocation().getX();
						subz1 = b.getLocation().getZ();	
					}			
				}
			}
			
			Location l = new Location(world, subx1, 71, subz1);
			l.getBlock().setType(Material.ACACIA_LOG);
			
			
			double xul = smallestx;
			double xur = 0d;
			double xbl = 0d;
			double xbr = 0d;
			
			double zul = smallestz;
			double zur = 0d;
			double zbl = 0d;
			double zbr = 0d;
			for(int ix = 0; ix != blocks_copy.size(); ix++) {
				for(Block b : blocks_copy) {
					if(b.getType().toString().contains("BRICK")) {
						if(b.getLocation().getX() == (subx1+ix) && b.getLocation().getZ() == subz1) {
							b.getLocation().clone().add(0,4,0).getBlock().setType(Material.BEDROCK);
							xur = b.getLocation().getX();
							zur = b.getLocation().getZ();
						}
					}
				}		
			}
			for(int iz = 0; iz != blocks_copy.size(); iz++) {
				for(Block b : blocks_copy) {
					if(b.getType().toString().contains("BRICK")) {						
						if(b.getLocation().getX() == (subx1) && b.getLocation().getZ() == subz1+iz) {
							b.getLocation().clone().add(0,4,0).getBlock().setType(Material.BEDROCK);
							xbl = b.getLocation().getX();
							zbl = b.getLocation().getZ();
						}
					}
				}		
			}
			for(int ix = 0; ix != blocks_copy.size(); ix++) {
				for(Block b : blocks_copy) {
					if(b.getType().toString().contains("BRICK")) {						
						if(b.getLocation().getX() == (xbl+ix) && b.getLocation().getZ() == zbl) {
							b.getLocation().clone().add(0,4,0).getBlock().setType(Material.BEDROCK);
							xbr = b.getLocation().getX();
							zbr = b.getLocation().getZ();
						}
					}
				}		
			}
			Location lul = new Location(loc1.getWorld(), xul, 74, zul);
			Location lur = new Location(loc1.getWorld(), xur, 75, zur);
			Location lbl = new Location(loc1.getWorld(), xbl, 76, zbl);
			Location lbr = new Location(loc1.getWorld(), xbr, 77, zbr);
			
			lul.getBlock().setType(Material.RED_WOOL);
			lur.getBlock().setType(Material.YELLOW_WOOL);
			lbl.getBlock().setType(Material.GREEN_WOOL);
			lbr.getBlock().setType(Material.BLUE_WOOL);
			times++;
		}
		
		
		
		
		
		
//		while((blocks_copy.size()-blocks_red.size()) > 0 && times < 100) {
//			broadcast("Copy Size: "+blocks_copy.size());
//			for(Block b : blocks_copy) {
//				if(b.getType().toString().contains("BRICK")) {
//					if(ax == -0d && az == -0d) {
//						broadcast("Ax set!");
//						ax = b.getLocation().getX();
//						az = b.getLocation().getZ();
//						subx1 = ax;
//						subz1 = az;
//						subz2 = az;
//					}else if((int)b.getLocation().getZ() == (int)subz1){
//						broadcast("if("+(int)b.getLocation().getZ()+" == "+(int)subz1);
//						broadcast("Addet to Reds");
//						subx2 = b.getLocation().getX();
//						blocks_red.add(b);
//						b.getLocation().clone().add(0,10,0).getBlock().setType(Material.BEDROCK);
//					}
//				}else {
//					subz2++;
//				}
//			}
//			times++;
//		}
		
//		while(blocks_copy.size() > 0) {
//			broadcast("Size of Blocky_copy: "+blocks_copy.size());
//			for(int i = 0; i != blocks_copy.size(); i++) {
//				if(ax == -0d) ax = blocks_copy.get(0).getLocation().getX(); //Init
//				if(az == -0d) az = blocks_copy.get(0).getLocation().getZ(); //Init
//				
//				loc = blocks_copy.get(i).getLocation();
//				
////				if(loc.getX() != ax)
//			}
//			
//			
//			
//			
//			for(Block block : blocks_copy) {
//				loc = block.getLocation();
//				/*
//				 * Es wird in folgende Richtungen gemessen:
//				 * links oben nach rechts oben nach unten rechts nach unten links nach oben link
//				 */
////				for(Block b : blocks) {
////					if(b.getLocation().getX() < ax) ax = b.getLocation().getX(); //kleinsten X Wert bestimmen
////					if(b.getLocation().getZ() < az) az = b.getLocation().getZ(); //kleinsten Z Wert bestimmen
////				}
//				
//				blocks_copy.remove(block); //Block aus der LIste entfernen, da er einer SubArea zugeteilt wurde
//			}
//		}
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
	
	public class SubArea {
		
		public Location location = null;
		public Location location2 = null;
		
		public SubArea(Location location, Location location2) {
			this.location = location;
			this.location2 = location2;
		}
		
	}
	
}
