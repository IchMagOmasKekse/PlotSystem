package me.teamdream.de.plotmanager;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.teamdream.de.Cuboid;
import me.teamdream.de.PlotSystem;

public class PlotSession {
	
	public Player host = null;
	
	public Location pos1 = null;
	public Location pos2 = null;
	public Location sign_position = null;
	public BossBar bossbar = null;
	public Cuboid cuboid = null;
	public String title = "PlotSession[Region(§bPos1§f: §3keine Pos1 §bPos2§f: §3keine Pos2§f), Schild(§bPos§f: §3keine Position§f)] §7Nutze '/plot <PlotID> create' um ein Plot zu erstellen";
	
	public PlotSession(Player p) {
		this.host = p;
		bossbar = Bukkit.createBossBar(title, BarColor.PURPLE, BarStyle.SOLID);
		bossbar.addPlayer(p);
		bossbar.setVisible(true);
	}
	
	public void closeSession() {
		bossbar.removePlayer(host);
		host.sendMessage("§7§o[Deine PlotSession wurde beendet]");
		if(PlotSystem.getPlotManager().knowsSession(host)) PlotSystem.getPlotManager().removeSession(host);
	}
	
	public void setPos1(Location pos1) {
		this.pos1 = pos1.clone();//.add(0.5, 1, 0.5)
		playParticles(pos1.clone(), Effect.ENDER_SIGNAL);
		setBossbar();
		if(pos2 != null) createCuboid();
	}
	public void setPos2(Location pos2) {
		this.pos2 = pos2.clone();
		this.pos2.add(-1,0,-1);
		playParticles(pos2.clone(), Effect.ENDER_SIGNAL);
		setBossbar();
		if(pos1 != null) createCuboid();
		
	}
	public void setSignPosition(Location pos) {
		this.sign_position = pos.clone();		
		setBossbar();
	}
	public Location getPos1() {
		return pos1.clone();
	}
	public Location getPos2() {
		return pos2.clone();
	}
	public Location getSignPosition() {
		return sign_position.clone();
	}
	public Player getHost() {
		return host;
	}
	public void createCuboid() {
//		sortLocations();
		cuboid = new Cuboid(pos1.clone(), pos2.clone());
////		pos1 = cuboid.getLowerNE();
////		pos2 = cuboid.getUpperSW();
//		pos1 = new Location(cuboid.getWorld(), cuboid.getUpperX(), 256, cuboid.getUpperZ());
//		pos2 = new Location(cuboid.getWorld(), cuboid.getLowerX(), 0, cuboid.getLowerZ());
	}
	
	public void playParticles(Location loc, Effect particle) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getWorld().getName().equals(loc.getWorld().getName())) {
				p.getWorld().playEffect(loc, particle, 3);
			}
		}
	}
	public void setBossbar() {
		if(pos1 == null && pos2 == null && sign_position == null) bossbar.setTitle(title);
		else if(pos1 == null && pos2 == null && sign_position != null) bossbar.setTitle(title.replace("§3keine Position", sign_position.getX()+"/"+sign_position.getY()+"/"+sign_position.getZ()));
		else if(pos1 == null && pos2 != null && sign_position == null) bossbar.setTitle(title.replace("§3keine Pos2", pos2.getX()+"/"+pos2.getY()+"/"+pos2.getZ()));
		else if(pos1 != null && pos2 == null && sign_position == null) bossbar.setTitle(title.replace("§3keine Pos1", pos1.getX()+"/"+pos1.getY()+"/"+pos1.getZ()));
		else if(pos1 != null && pos2 != null && sign_position == null) {
			bossbar.setTitle(title
					.replace("§3keine Pos1", pos1.getX()+"/"+pos1.getY()+"/"+pos1.getZ())
					.replace("§3keine Pos2", pos2.getX()+"/"+pos2.getY()+"/"+pos2.getZ()));
		}else if(pos1 != null && pos2 != null && sign_position != null) {
			bossbar.setTitle(title
					.replace("§3keine Pos1", pos1.getX()+"/"+pos1.getY()+"/"+pos1.getZ())
					.replace("§3keine Pos2", pos2.getX()+"/"+pos2.getY()+"/"+pos2.getZ())
					.replace("§3keine Position", sign_position.getX()+"/"+sign_position.getY()+"/"+sign_position.getZ()));
		}else if(pos1 != null && pos2 == null && sign_position != null) {
			bossbar.setTitle(title
					.replace("§3keine Pos1", pos1.getX()+"/"+pos1.getY()+"/"+pos1.getZ())
					.replace("§3keine Position", sign_position.getX()+"/"+sign_position.getY()+"/"+sign_position.getZ()));
		}else if(pos1 == null && pos2 != null && sign_position != null) {
			bossbar.setTitle(title
					.replace("§3keine Pos2", pos2.getX()+"/"+pos2.getY()+"/"+pos2.getZ())
					.replace("§3keine Position", sign_position.getX()+"/"+sign_position.getY()+"/"+sign_position.getZ()));
		}
	}
	
	public void sortLocations() {
		//    _____ C
		//   |    /| 
		//   |  /  | b
		//   |/____|
		//  A   a  
		
		Location bigger = null;
		Location smaller = null;
		
		boolean success = false;
		
		if(pos1.getX() == pos2.getX() && pos1.getZ() == pos2.getZ()) {
		//Passiert nix
		}else if(pos1.getX() < pos2.getX()) {
			bigger = pos2.clone();
			smaller = pos1.clone();
			success = true;
		}else if(pos1.getX() > pos2.getX()) {
			bigger = pos1.clone();
			smaller = pos2.clone();
			success = true;
		}else if(pos1.getZ() < pos2.getZ()) {
			bigger = pos2.clone();
			smaller = pos1.clone();
			success = true;
		}else if(pos1.getZ() > pos2.getZ()) {
			bigger = pos1.clone();
			smaller = pos2.clone();
			success = true;
		}/* Y-Integration -> */ else if(pos1.getY() < pos2.getY()) {
			bigger = pos2.clone();
			smaller = pos1.clone();
			success = true;
		}else if(pos1.getY() > pos2.getY()) {
			bigger = pos1.clone();
			smaller = pos2.clone();
			success = true;
		}
		
		if(success == false) {
			double a = pos1.getX()-pos2.getX();
			double b = pos1.getZ()-pos2.getZ();
			double diagonal = 0d;
			if(pos1.getX() > pos2.getX()) a = (pos1.getX()-pos2.getX());
			else a = (pos2.getX()-pos1.getX());
			if(pos1.getZ() > pos2.getZ()) b = (pos1.getZ()-pos2.getZ());
			else b = (pos2.getZ()-pos1.getZ());
			
			diagonal = Math.sqrt((Math.pow(a, 2) + Math.pow(b, 2)));
			Bukkit.broadcastMessage("XYZ 1: "+pos1.getX()+"/"+pos1.getY()+"/"+pos1.getZ());
			Bukkit.broadcastMessage("XYZ 1: "+pos2.getX()+"/"+pos2.getY()+"/"+pos2.getZ());
			Bukkit.broadcastMessage("Die Diagonale beträgt §c"+diagonal+" §fBlöcke");			
		}else {
			pos2 = bigger;
			pos1 = smaller;
			Bukkit.broadcastMessage("XYZ 2: "+pos1.getX()+"/"+pos1.getY()+"/"+pos1.getZ());
			Bukkit.broadcastMessage("XYZ 2: "+pos2.getX()+"/"+pos2.getY()+"/"+pos2.getZ());
			Bukkit.broadcastMessage("Wurde sortiert!");	
			return;
		}
		
	}
	
}
