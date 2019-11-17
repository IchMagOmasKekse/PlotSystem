package me.teamdream.de.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.teamdream.de.plotmanager.PlotProfile;

public class PlotBuyEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private PlotProfile profile = null;
	private Player buyer = null;
	private int plotPrice = 0;
	private boolean wasSuccesfful = false;
	
	public PlotBuyEvent(PlotProfile profile, Player buyer, int plotPrice, boolean wasSuccessful) {
		this.profile = profile;
		this.buyer = buyer;
		this.plotPrice = plotPrice;
		this.wasSuccesfful = wasSuccessful;
	}
	
	public PlotProfile getProfile() {
		return profile;
	}
	public Player getBuyer() {
		return buyer;
	}
	public int getPlotPrice() {
		return plotPrice;
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
