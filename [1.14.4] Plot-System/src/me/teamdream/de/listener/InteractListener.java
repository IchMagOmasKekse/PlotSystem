package me.teamdream.de.listener;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import me.teamdream.de.PlotSystem;
import me.teamdream.de.plotmanager.PlotManager.PlotID;
import me.teamdream.de.plotmanager.PlotProfile;

public class InteractListener implements Listener {
	
	public InteractListener() {
		PlotSystem.getInstance().getServer().getPluginManager().registerEvents(this, PlotSystem.getInstance());
	}
	
	private Material checkedMaterial = Material.valueOf("BRICKS");
	private Material resettedMaterial = Material.OAK_FENCE;
	private boolean reset = false;
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
				Player p = e.getPlayer();
				if(p.hasPermission("plotsystem.createplot")) {
					Sign sign = (Sign) e.getClickedBlock().getState();
					if(sign == null) p.sendMessage("Sign == null");
					if(sign.getLine(2).equals("RIGHT=") && sign.getLine(3).equals("RESET")) {
						reset = true;
					}else reset = false;
					if(sign.getLine(0).equals("CREATE PLOT")) {
//						p.sendMessage("§7Sammle Informationen..");
						if(sign.getLine(1).contains("ID:")) {
							String[] a = sign.getLine(1).split(":");
							String id = a[1];
//							p.sendMessage("§7TYP: §bPlot Erstellen");
//							p.sendMessage("§7ID: §b"+id);
							PlotID plotid = new PlotID(sign.getLocation());
							plotid.setID(id);
							if(e.getClickedBlock().getType().toString().contains("WALL_SIGN")) {
								Block schildHalter = e.getClickedBlock().getRelative(e.getBlockFace().getOppositeFace());
								if(schildHalter.getType().toString().endsWith("_FENCE") || schildHalter.getType().toString().endsWith(checkedMaterial.toString())) {
									Location pointer = schildHalter.getLocation();
									
									p.sendMessage("§7Erfasse Umrandung des Plots...");

									Vector vec = getDirection(pointer.clone());
									Location a1 = pointer.clone();
									if(reset == false)a1.getBlock().setType(checkedMaterial);
									else a1.getBlock().setType(resettedMaterial);
									
									playParticles(a1, Effect.MOBSPAWNER_FLAMES);
									PlotSystem.getInstance().getPlotManager().registerPlot(plotid);
									PlotProfile plotProfile  = PlotSystem.getInstance().getPlotManager().getPlotProfile(plotid);
									plotProfile.addBlock(a1.getBlock());
									if(plotProfile != null) {
										for(int i1 = 0; i1 != 999; i1++) {
											if(reset == false)vec = getDirection(a1);
											else vec = getDirectionResetter(a1);
											if(vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0) {
												break;
											} else {
												a1.add(vec);
												if(reset == false) {
													plotProfile.addBlock(a1.getBlock());
													a1.getBlock().setType(checkedMaterial);
												}else a1.getBlock().setType(resettedMaterial);
												playParticles(a1, Effect.MOBSPAWNER_FLAMES);
											}
										}
										
										///////
										plotProfile.filterRectangles();
										///////										
										
										if(PlotSystem.getInstance().getPlotManager().existPlot(plotid)) {
											if(reset == false)p.sendMessage("§fPlot wurde erfasst!");
											else p.sendMessage("§cDas Plot wurde resettet!");
											if(reset == false) {
												sign.setLine(0, "CREATE PLOT");
												sign.setLine(1, "ID:"+id);
												sign.setLine(2, "RIGHT=");
												sign.setLine(3, "RESET");
												sign.update();
											}else {
												sign.setLine(0, "CREATE PLOT");
												sign.setLine(1, "ID:"+id);
												sign.setLine(2, "");
												sign.setLine(3, "");
												sign.update();
											}
										}else p.sendMessage("§cPlot konnte nicht erfasst werden");
									} else p.sendMessage("§cEtwas ist schief gelaufen.. Versuchs nochmal!\nOder es existiert bereits ein Plot mit diesen Namen.");
								}else {
									p.sendMessage("§cPlot konnte nicht erstellt werden, da es bereits erstellt wurde!\nErsetze alle Bricks mit Holz Zäunen, um die Simulation erneut abzuspielen");
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void playParticles(Location loc, Effect particle) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getWorld().getName().equals(loc.getWorld().getName())) {
				p.getWorld().playEffect(loc, particle, 3);
			}
		}
	}
	
	public Vector getDirection(Location pointer) {
		if(pointer.clone().add(1,0,0).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(1,0,0);
			
		}else if(pointer.clone().add(0,0,1).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(0,0,1); 
			
		}else if(pointer.clone().add(-1,0,0).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(-1,0,0); 
			
		}else if(pointer.clone().add(0,0,-1).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(0,0,-1);
		}
		
		if(pointer.clone().add(0,1,0).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(0,1,0);
			
		}else if(pointer.clone().add(1,1,0).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(1,1,0);
			
		}else if(pointer.clone().add(0,1,1).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(0,1,1); 
			
		}else if(pointer.clone().add(-1,1,0).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(-1,1,0); 
			
		}else if(pointer.clone().add(0,1,-1).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(0,1,-1);
			
		}
		
		if(pointer.clone().add(0,-1,0).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(0,-1,0);
			
		}else if(pointer.clone().add(1,-1,0).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(1,-1,0);
			
		}else if(pointer.clone().add(0,-1,1).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(0,-1,1); 
			
		}else if(pointer.clone().add(-1,-1,0).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(-1,-1,0); 
			
		}else if(pointer.clone().add(0,-1,-1).getBlock().getType().toString().endsWith("_FENCE")) {
			return new Vector(0,-1,-1);
			
		}else return new Vector(0,0,0);

	}
	public Vector getDirectionResetter(Location pointer) {
		String s = "BRICKS";
		if(pointer.clone().add(1,0,0).getBlock().getType().toString().endsWith(s)) {
			return new Vector(1,0,0);
			
		}else if(pointer.clone().add(0,0,1).getBlock().getType().toString().endsWith(s)) {
			return new Vector(0,0,1); 
			
		}else if(pointer.clone().add(-1,0,0).getBlock().getType().toString().endsWith(s)) {
			return new Vector(-1,0,0); 
			
		}else if(pointer.clone().add(0,0,-1).getBlock().getType().toString().endsWith(s)) {
			return new Vector(0,0,-1);
		}
		
		if(pointer.clone().add(0,1,0).getBlock().getType().toString().endsWith(s)) {
			return new Vector(0,1,0);
			
		}else if(pointer.clone().add(1,1,0).getBlock().getType().toString().endsWith(s)) {
			return new Vector(1,1,0);
			
		}else if(pointer.clone().add(0,1,1).getBlock().getType().toString().endsWith(s)) {
			return new Vector(0,1,1); 
			
		}else if(pointer.clone().add(-1,1,0).getBlock().getType().toString().endsWith(s)) {
			return new Vector(-1,1,0); 
			
		}else if(pointer.clone().add(0,1,-1).getBlock().getType().toString().endsWith(s)) {
			return new Vector(0,1,-1);
			
		}
		
		if(pointer.clone().add(0,-1,0).getBlock().getType().toString().endsWith(s)) {
			return new Vector(0,-1,0);
			
		}else if(pointer.clone().add(1,-1,0).getBlock().getType().toString().endsWith(s)) {
			return new Vector(1,-1,0);
			
		}else if(pointer.clone().add(0,-1,1).getBlock().getType().toString().endsWith(s)) {
			return new Vector(0,-1,1); 
			
		}else if(pointer.clone().add(-1,-1,0).getBlock().getType().toString().endsWith(s)) {
			return new Vector(-1,-1,0); 
			
		}else if(pointer.clone().add(0,-1,-1).getBlock().getType().toString().endsWith(s)) {
			return new Vector(0,-1,-1);
			
		}else return new Vector(0,0,0);
		
	}
	
}
