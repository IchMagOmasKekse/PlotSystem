package me.teamdream.de.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.teamdream.de.plotmanager.PlotProfile;

public class PlotLeaveEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private PlotProfile profile = null;
	private Player guest = null;
	
	public PlotLeaveEvent(PlotProfile profile, Player guest) {
		this.profile = profile;
		this.guest = guest;
	}
	
	public PlotProfile getProfile() {
		return profile;
	}
	public Player getGuest() {
		return guest;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
