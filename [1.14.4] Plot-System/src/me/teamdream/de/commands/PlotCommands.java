package me.teamdream.de.commands;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.teamdream.de.PlotSystem;
import me.teamdream.de.event.PlotBuyEvent;
import me.teamdream.de.event.PlotCreateEvent;
import me.teamdream.de.inventory.PlotListInventory;
import me.teamdream.de.plotmanager.PlotManager;
import me.teamdream.de.plotmanager.PlotManager.PlotID;
import me.teamdream.de.plotmanager.PlotProfile;
import me.teamdream.de.plotmanager.PlotSession;

public class PlotCommands implements CommandExecutor {
	
	private String noPerm = "§cDu hast kein Recht dazu!";
	private PlotManager pmanager = PlotSystem.getPlotManager();
	private PlotProfile pprofile = null;
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		pprofile = null;
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("plot")) {
				if(p.hasPermission("plotsystem.plot")) {
					switch(args.length) {
					case 0:
						sendHelp(p);
						break;
					case 1:
						if(args[0].equalsIgnoreCase("sell")) {
							if(p.hasPermission("plotsystem.sell")) {
								sellPlot(p);
							}else p.sendMessage(noPerm);
						}else if(args[0].equalsIgnoreCase("auto")) {
							if(p.hasPermission("plotsystem.auto")) {
								searchFreePlot(p);
							}else p.sendMessage(noPerm);
						}else if(args[0].equalsIgnoreCase("closesession")) {
							if(p.hasPermission("plotsystem.closesession")) {
								PlotSession session = pmanager.getSession(p);
								if(session != null) session.closeSession();
							}else p.sendMessage(noPerm);
						}else if(args[0].equalsIgnoreCase("members")) {
							if(p.hasPermission("plotsystem.members")) {
								pprofile = PlotSystem.getCurrentPlot(p.getLocation());
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
							}else p.sendMessage(noPerm);
						}else if(args[0].equalsIgnoreCase("displayname")) {
							if(p.hasPermission("plotsystem.displayname")) {
								PlotProfile profile = PlotSystem.getCurrentPlot(p.getLocation());
								if(profile == null) {
									p.sendMessage("§cDu bist in der Wildnis. Stelle dich auf das Plot, welches du bearbeiten möchtest!");
									return false;
								}else{
									p.sendMessage("§bDisplayname vom Plot §f"+profile.plotid.getID()+" §blautet:\n§7-"+profile.getDisplayname());
								}
							}else p.sendMessage(noPerm);
						}else if(args[0].equalsIgnoreCase("plots")) {
							if(p.hasPermission("plotsystem.plots")) {
								PlotListInventory pinv = new PlotListInventory();
								p.openInventory(pinv.getInventory(p));
							}else p.sendMessage(noPerm);
						}
						break;
					case 2:
						if(args[1].equalsIgnoreCase("create")) {
							if(p.hasPermission("plotsystem.create")) {
								PlotSession session = pmanager.registerSession(p);
								if(session == null) {
									p.sendMessage("§cDu musst erst einen Bereich markieren!");
								}else if(session.pos1 != null && session.pos2 != null && session.sign_position != null){
									PlotID plotid = new PlotID(session.getSignPosition());
									plotid.setID(args[0]);
									if(plotid.getLocation() == null) p.sendMessage("§eDu musst das Schild noch mit §6SNEAKEN + Linksklick §emarkieren.\nWelches Schild? Na das wo der Preis drauf steht!");
									else {
										if(pmanager.registerPlot(plotid, session)) {
											p.sendMessage("§aPlot §f"+plotid.getID()+" §awurde erstellt");
											session.closeSession();
											pmanager.sessions.remove(p);
											pprofile = PlotSystem.getCurrentPlot(p.getLocation());
											Bukkit.getPluginManager().callEvent(new PlotCreateEvent(pprofile, p, true));
										}else {
											Bukkit.getPluginManager().callEvent(new PlotCreateEvent(null, p, false));
											p.sendMessage("§cDiesen Plot gibt es bereits");
										}
									}
								}else p.sendMessage("§cDu musst 2 Locations und das Schild markieren mit SNEAKEN+LINKSKLICK!");
							}else p.sendMessage(noPerm);
						}else if(args[1].equalsIgnoreCase("subregion")) {
							if(p.hasPermission("plotsystem.subregion")) {
								PlotSession session = pmanager.getSession(p);
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
										pmanager.sessions.remove(p);
									}else p.sendMessage("§cDiese Sub-region existiert bereits");								
								}else p.sendMessage("§cDu musst 2 Locations markieren!");
							}else p.sendMessage(noPerm);
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
							}else p.sendMessage(noPerm);
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
							}else p.sendMessage(noPerm);
						}
						break;
					}
					
					
					if(args.length >= 2) {
						if(args[0].equalsIgnoreCase("displayname")) {
							if(p.hasPermission("plotsystem.displayname")) {
								if(p.isOp()) {
									PlotProfile profile = PlotSystem.getCurrentPlot(p.getLocation());
									if(profile == null) {
										p.sendMessage("§cDu bist in der Wildnis. Stelle dich auf das Plot, welches du bearbeiten möchtest!");
										return false;
									}else{
										String name = "";
										for(int i = 1; i < args.length; i++) {
											if(i == 1) name = args[i];
											else name = name + " " + args[i];
										}
										if(profile.setDisplayname(name)) p.sendMessage("§7§oNeuer Displayname wurde gesetzt");
										else p.sendMessage("§c§oNeuer Displayname konnte nicht gesetzt werden!");
									}
								}else {
									PlotProfile profile = PlotSystem.getCurrentPlot(p.getLocation());
									if(profile == null) {
										p.sendMessage("§cDu bist in der Wildnis. Stelle dich auf das Plot, welches du bearbeiten möchtest!");
										return false;
									}else if(pmanager.getOwner(profile.plotid) != null && pmanager.getOwner(profile.plotid).equals(p.getUniqueId())){
										String name = "";
										for(int i = 1; i < args.length; i++) {
											if(i == 1) name = args[i];
											else name = name + " " + args[i];
										}
										if(profile.setDisplayname(name)) p.sendMessage("§7§oNeuer Displayname wurde gesetzt");
										else p.sendMessage("§c§oNeuer Displayname konnte nicht gesetzt werden!");
									}else p.sendMessage("§c§oDies ist nicht dein eigenes Plot!");
								}
							}else p.sendMessage(noPerm);
						}
					}
				}else p.sendMessage(noPerm);
			}else p.sendMessage(noPerm);
		}
		return true;
	}
	private Random r = new Random();
	public void searchFreePlot(Player p) {
		int index = r.nextInt(pmanager.plots.size());
		int processed = 0;
		boolean found = true;
		PlotProfile selectedProfile = null;
		ArrayList<PlotProfile> profiles = new ArrayList<PlotProfile>();
		for(PlotProfile pp : pmanager.plots.values()) {
			profiles.add(pp);
		}
		while(pmanager.getOwner(profiles.get(index).plotid) != null) {
			index = r.nextInt(profiles.size());
			processed++;
			if(processed == profiles.size()) {
				found = false;
				break;
			}
		}
		selectedProfile = profiles.get(index);
		if(found) {
			Location loc = selectedProfile.plotregion.getSpawnMiddle();
			loc.setYaw(p.getLocation().clone().getYaw());
			loc.setPitch(p.getLocation().clone().getPitch());
			p.teleport(loc);
			p.sendMessage("§9Dieses Plot ist noch frei");
		}else p.sendMessage("§cEs konnte kein freies Plot gefunden werden.\nProbiere es erneut, wenn es wieder freie Plots gibt.");
	}
	
	public void sellPlot(Player p) {
		sellPlot(p, true);
	}
	
	public void sellPlot(Player p, boolean currentStayingPlot) {
		PlotProfile pp = PlotSystem.getCurrentPlot(p.getLocation());
		if(pp == null) {
			p.sendMessage("§cDu bist in der Wildnis. Stelle dich auf das Plot, welches du verkaufen möchtest!");
		}else {
			if(pmanager.getOwner(pp.plotid) != null){				
				if(pmanager.getOwner(pp.plotid).equals(p.getUniqueId())) {
					if(pmanager.sellOwnPlot(p, pp.plotid)) {
						Bukkit.getPluginManager().callEvent(new PlotBuyEvent(pp, p, pp.preis, true));
						p.sendMessage("§aDein Plot wurde verkauft!");
					}else {
						Bukkit.getPluginManager().callEvent(new PlotBuyEvent(pp, p, pp.preis, false));
						p.sendMessage("§cDein Plot konnte nicht verkauft werden");
					}
				}
			}else p.sendMessage("§cDieses Plot wurde von niemanden beansprucht!");
		}
	}
	
	public void sendHelp(Player p) {
		p.sendMessage("§bPlotSystem Commands");
		p.sendMessage(" §f/plot ... §bFunktion");
		p.sendMessage(" §fclosesession §bBeende deine PlotSession");
		p.sendMessage(" §fauto §bSucht dir ein zufälliges Plot zum Kaufen aus");
		p.sendMessage(" §f[PlotID] create §bErstelle ein Plot");
		p.sendMessage(" §f[PlotID] subregion §bAdde eine Unterregion zu einem Plot");
		p.sendMessage(" §f[PlotID] info §bZeigt alle Infos eines Plots");
		p.sendMessage(" §faddmember <Player> §bAdde einen Spieler zu einem Plot");
		p.sendMessage(" §fremovemember <Player> §bEntferne einen Spieler von einem Plot");
		p.sendMessage(" §fdisplayname §bZeigt dir den Displaynamen des Plots an");
		p.sendMessage(" §fdisplayname [Displayname] §bSetzt einen neuen Displayname");
	}
	
}
