package de.dakror.liturfaliarcest.game.item;

import java.util.HashMap;

import de.dakror.liturfaliarcest.settings.Attributes;

/**
 * @author Dakror
 */
public class Item
{
	public static HashMap<Integer, Item> items;
	
	public static int SIZE = 32;
	
	public static void init()
	{	
		
	}
	
	String name;
	Attributes attr;
	int id;
	public int tx, ty;
	
	public Item(int id, int tx, int ty, String name)
	{
		this.id = id;
		this.tx = tx;
		this.ty = ty;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Attributes getAttributes()
	{
		return attr;
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
}
