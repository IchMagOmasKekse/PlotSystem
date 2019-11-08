package me.teamdream.de.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.teamdream.de.inventory.PlotListInventory;

public class Plots implements CommandExecutor {
	
	String noPerm = "§cDu hast kein Recht dazu!";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("plots")) {
				if(p.hasPermission("plotsystem.plots")) {
					PlotListInventory pinv = new PlotListInventory();
					p.openInventory(pinv.getInventory(p));
				}else p.sendMessage(noPerm);
			}
		}
		return true;
	}
	
}
