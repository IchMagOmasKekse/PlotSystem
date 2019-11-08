package me.teamdream.de;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.teamdream.de.plotmanager.PlotProfile;

public class PlayerLocator {
	
	public Player player = null;
	public PlotProfile profile = null;
	public BossBar bossbar = null;
	public boolean send_exit_message = false;
	public boolean send_entry_message = false;
	
	public PlayerLocator(Player player) {
		this.player = player;
		bossbar = Bukkit.createBossBar("§2Du bist in der Wildnis", BarColor.GREEN,  BarStyle.SOLID);
		bossbar.addPlayer(player);
	}
	
	String plot_name = "";
	
	public void updatePlot(PlotProfile profile) {
		if(profile == null) {
			bossbar.setTitle("§fWildnis");
			bossbar.addPlayer(player);			
		} else {
			if(this.profile == null)	this.profile = profile;
			if(!this.profile.plotid.getID().equals(profile.plotid.getID())) {
				if(send_exit_message)player.sendMessage("§7§o[Du hast "+this.profile.plotid.getID()+" verlassen]");
				if(send_entry_message)player.sendMessage("§7§o[Du hast "+profile.plotid.getID()+" betreten]");
				this.profile = profile;
			}
			if(profile.getDisplayname().equals("NO_DISPLAYNAME") || profile.getDisplayname().toUpperCase().equals("NONE")) plot_name = profile.plotid.getID();
			else plot_name = profile.getDisplayname();
			if(PlotSystem.getPlotManager().getOwner(profile.plotid) != null) bossbar.setTitle("§f"+plot_name+" §2von §f"+Bukkit.getOfflinePlayer(PlotSystem.getPlotManager().getOwner(profile.plotid)).getName());
			else bossbar.setTitle("§f"+plot_name+" §7§OInfo: Kein Besitzer");
			bossbar.addPlayer(player);
		}
	}
	
	public void close() {
		bossbar.removeAll();
	}
	
}
