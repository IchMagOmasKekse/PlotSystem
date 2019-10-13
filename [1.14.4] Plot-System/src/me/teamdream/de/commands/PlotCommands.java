package me.teamdream.de.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.teamdream.de.PlotSystem;
import me.teamdream.de.plotmanager.PlotManager.PlotID;

public class PlotCommands implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("plot")) {
				switch(args.length) {
				case 0:
					p.sendMessage("[TEST] Area wird erstellt von deiner Location bis deiner Location + 10 Blöcken in jede Richtung");
					if(PlotSystem.getInstance().getPlotManager().registerPlot(new PlotID(p.getLocation()), p.getLocation(), p.getLocation().add(10,10,10))) {
						p.sendMessage("§aDas Plot wurde erstellt");
					}else p.sendMessage("§cDas Plot konnte nicht erstellt werden");
					break;
				}
			}
		}
		return true;
	}
	
}
