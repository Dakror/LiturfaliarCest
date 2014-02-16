package de.dakror.liturfaliarcest.util;

import de.dakror.liturfaliarcest.game.entity.Entity;

/**
 * @author Dakror
 */
public class Event
{
	Entity source;
	
	public Event(Entity source)
	{
		this.source = source;
	}
	
	public Entity getSource()
	{
		return source;
	}
}
