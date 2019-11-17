package me.teamdream.de.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.teamdream.de.plotmanager.PlotProfile;

public class PlotEntryEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private PlotProfile profile = null;
	private Player guest = null;
	private boolean isBanned = false;
	
	public PlotEntryEvent(PlotProfile profile, Player guest, boolean isBanned) {
		this.profile = profile;
		this.guest = guest;
		this.isBanned = isBanned;
	}
	
	public PlotProfile getProfile() {
		return profile;
	}
	public Player getGuest() {
		return guest;
	}
	public boolean isBanned() {
		return isBanned;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
