package me.teamdream.de.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.teamdream.de.plotmanager.PlotProfile;

public class PlotSellEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private PlotProfile profile = null;
	private Player seller = null;
	private int plotPrice = 0;
	private boolean wasSuccesfful = false;
	
	public PlotSellEvent(PlotProfile profile, Player seller, int plotPrice, boolean wasSuccessful) {
		this.profile = profile;
		this.seller = seller;
		this.plotPrice = plotPrice;
		this.wasSuccesfful = wasSuccessful;
	}
	
	public PlotProfile getProfile() {
		return profile;
	}
	public Player getSeller() {
		return seller;
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
