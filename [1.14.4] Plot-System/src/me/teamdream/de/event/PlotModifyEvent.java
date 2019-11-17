package me.teamdream.de.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.teamdream.de.plotmanager.PlotProfile;

public class PlotModifyEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private PlotProfile profile = null;
	private Player actor = null;
	private ModifyType modifyType = ModifyType.UNDEFINED;
	
	public PlotModifyEvent(PlotProfile profile, Player actor, ModifyType modifyType) {
		this.profile = profile;
		this.modifyType = modifyType;
	}
	
	public PlotProfile getProfile() {
		return profile;
	}
	public Player getActor() {
		return actor;
	}
	public ModifyType getModifyType() {
		return modifyType;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public enum ModifyType {
		
		UNDEFINED,
		BREAK,
		PLACE,
		INTERACT,
		ITEM_DROP;
		
	}
}
