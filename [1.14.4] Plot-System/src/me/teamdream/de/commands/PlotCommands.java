package me.teamdream.de.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.teamdream.de.PlotSystem;
import me.teamdream.de.inventory.PlotListInventory;
import me.teamdream.de.plotmanager.PlotManager.PlotID;
import me.teamdream.de.plotmanager.PlotProfile;
import me.teamdream.de.plotmanager.PlotSession;

public class PlotCommands implements CommandExecutor {
	
	String noPerm = "§cDu hast kein Recht dazu!";
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("plot")) {
				if(p.hasPermission("plotsystem.plot")) {
					switch(args.length) {
					case 0:
						sendHelp(p);
						break;
					case 1:
						if(args[0].equalsIgnoreCase("closesession")) {
							if(p.hasPermission("plotsystem.closesession")) {
								PlotSession session = PlotSystem.getInstance().getPlotManager().getSession(p);
								if(session != null) session.closeSession();
							}else p.sendMessage(noPerm);
						}else if(args[0].equalsIgnoreCase("members")) {
							if(p.hasPermission("plotsystem.members")) {
								PlotProfile pprofile = PlotSystem.getCurrentPlot(p.getLocation());
								if(pprofile == null) {
									p.sendMessage("§cDu bist in der Wildnis. Stelle dich auf das Plot, von dem du einen Spieler entfernen willst!");
									return false;
								}else{
									if(pprofile.getMembers().isEmpty()) p.sendMessage("§cEs gibt keine Member auf diesem Plot");
									else {										
										p.sendMessage("§aMember dieses Plots:");
										for(UUID uuid : pprofile.getMembers().keySet()) {
											p.sendMessage("§7- §f"+Bukkit.getOfflinePlayer(uuid).getName());
										}
									}
								}
							}else p.sendMessage(noPerm+"1");
						}else if(args[0].equalsIgnoreCase("plots")) {
							if(p.hasPermission("plotsystem.plots")) {
								PlotListInventory pinv = new PlotListInventory();
								p.openInventory(pinv.getInventory(p));
							}else p.sendMessage(noPerm+"1");
						}
						break;
					case 2:
						if(args[1].equalsIgnoreCase("create")) {
							if(p.hasPermission("plotsystem.create")) {
								PlotSession session = PlotSystem.getInstance().getPlotManager().registerSession(p);
								if(session == null) {
									p.sendMessage("§cDu musst erst einen Bereich markieren!");
								}else if(session.pos1 != null && session.pos2 != null && session.sign_position != null){
									PlotID plotid = new PlotID(session.getSignPosition());
									plotid.setID(args[0]);
									if(plotid.getLocation() == null) p.sendMessage("§eDu musst das Schild noch mit §6SNEAKEN + Linksklick §emarkieren.\nWelches Schild? Na das wo der Preis drauf steht!");
									else {
										if(PlotSystem.getInstance().getPlotManager().registerPlot(plotid, session)) {
											p.sendMessage("§aPlot §f"+plotid.getID()+" §awurde erstellt");
											session.closeSession();
											PlotSystem.getInstance().getPlotManager().sessions.remove(p);
										}else p.sendMessage("§cDiesen Plot gibt es bereits");
									}
								}else p.sendMessage("§cDu musst 2 Locations und das Schild markieren mit SNEAKEN+LINKSKLICK!");
							}else p.sendMessage(noPerm);
						}else if(args[1].equalsIgnoreCase("subregion")) {
							if(p.hasPermission("plotsystem.subregion")) {
								PlotSession session = PlotSystem.getInstance().getPlotManager().getSession(p);
								if(session == null) {
									p.sendMessage("§cDu musst erst einen Bereich markieren!");
								}else if(session.pos1 != null && session.pos2 != null){
									
									PlotProfile pprofile = PlotSystem.getCurrentPlot(p.getLocation());
									if(pprofile == null) {
										p.sendMessage("§cDu bist in der Wildnis. Stelle dich auf das Plot, zu dem du eine Subregion hinzufügen willst!");
										return false;
									}
									PlotID plotid = pprofile.plotid;
									if(pprofile.addSubRegion(session.pos1, session.pos2)) {
										p.sendMessage("§aEine Sub-Region wurde zu PlotID: §f"+plotid.getID()+" §ahinzugefügt");
										session.closeSession();
										PlotSystem.getInstance().getPlotManager().sessions.remove(p);
									}else p.sendMessage("§cDiese Sub-region existiert bereits");								
								}else p.sendMessage("§cDu musst 2 Locations markieren!");
							}else p.sendMessage(noPerm+"1");
						}else if(args[0].equalsIgnoreCase("addmember")) {
							if(p.hasPermission("plotsystem.addmember")) {
								PlotProfile pprofile = PlotSystem.getCurrentPlot(p.getLocation());
								if(pprofile == null) {
									p.sendMessage("§cDu bist in der Wildnis. Stelle dich auf das Plot, zu dem du einen Spieler hinzufügen willst!");
									return false;
								}else{
									UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
									if(pprofile.addMember(uuid)) {
										p.sendMessage("§aDu hast §7"+args[1]+" §azum Plot §7"+pprofile.plotid.getID()+" §ahinzugefügt!");
									}else p.sendMessage("§cDu kannst §7"+args[1]+" §cnicht zum Plot §7"+pprofile.plotid.getID()+" §chinzufügen, denn er ist bereits ein Member oder wurde vom Plot verbannt!");
								}
							}else p.sendMessage(noPerm+"1");
						}else if(args[0].equalsIgnoreCase("removemember")) {
							if(p.hasPermission("plotsystem.removemember")) {
								PlotProfile pprofile = PlotSystem.getCurrentPlot(p.getLocation());
								if(pprofile == null) {
									p.sendMessage("§cDu bist in der Wildnis. Stelle dich auf das Plot, von dem du einen Spieler entfernen willst!");
									return false;
								}else{
									UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
									if(pprofile.removeMember(uuid)) {
										p.sendMessage("§aDu hast §7"+args[1]+" §avom Plot §7"+pprofile.plotid.getID()+" §aentfernt!");
									}else p.sendMessage("§cDu kannst §7"+args[1]+" §cnicht vom Plot §7"+pprofile.plotid.getID()+" §centfernen, denn er ist kein Member vom Plot!");
								}
							}else p.sendMessage(noPerm+"1");
						}
						break;
					}
				}
			}
		}
		return true;
	}
	

	
	public void sendHelp(Player p) {
		p.sendMessage("§bPlotSystem Commands");
		p.sendMessage(" §f/plot ... §bFunktion");
		p.sendMessage(" §fclosesession §bBeende deine PlotSession");
		p.sendMessage(" §f[PlotID] create §bErstelle ein Plot");
		p.sendMessage(" §f[PlotID] subregion §bAdde eine Unterregion zu einem Plot");
		p.sendMessage(" §f[PlotID] info §bZeigt alle Infos eines Plots");
		p.sendMessage(" §faddmember <Player> §bAdde einen Spieler zu einem Plot");
		p.sendMessage(" §fremovemember <Player> §bEntferne einen Spieler von einem Plot");
	}
	
}
