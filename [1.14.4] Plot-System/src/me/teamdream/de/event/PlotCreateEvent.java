package me.teamdream.de.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.teamdream.de.plotmanager.PlotProfile;

public class PlotCreateEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private PlotProfile profile = null;
	private Player creator = null;
	private boolean wasSuccesfful = false;
	
	public PlotCreateEvent(PlotProfile profile, Player creator, boolean wasSuccessful) {
		this.profile = profile;
		this.creator = creator;
		this.wasSuccesfful = wasSuccessful;
	}
	
	public PlotProfile getProfile() {
		return profile;
	}
	public Player getCreator() {
		return creator;
	}
	public boolean wasSuccesfful() {
		return wasSuccesfful;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
