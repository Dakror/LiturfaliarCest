package de.dakror.liturfaliarcest.game.item;

import java.awt.Graphics2D;
import java.util.HashMap;

import de.dakror.gamesetup.util.CSVReader;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.settings.Attributes;
import de.dakror.liturfaliarcest.settings.Attributes.Attribute;

/**
 * @author Dakror
 */
public class Item
{
	public static HashMap<Integer, Item> items;
	
	public static int SIZE = 32;
	
	public static void init()
	{
		items = new HashMap<>();
		
		CSVReader csv = new CSVReader("/items.csv");
		String[] headings = csv.readRow();
		
		String cell = "";
		Item item = null;
		while ((cell = csv.readNext()) != null)
		{
			switch (csv.getIndex())
			{
				case 0:
					if (item != null) items.put(item.id, item);
					item = new Item();
					item.id = Integer.parseInt(cell);
					break;
				case 1:
					item.name = cell;
					break;
				case 2:
					item.tx = Integer.parseInt(cell);
					break;
				case 3:
					item.ty = Integer.parseInt(cell);
					break;
			}
			
			if (cell.length() == 0) continue;
			
			String h = headings[csv.getIndex()];
			if (Character.isUpperCase((h.charAt(0)))) item.attr.set(Attribute.valueOf(h), Float.parseFloat(cell));
		}
		
		items.put(item.id, item);
	}
	
	String name;
	Attributes attr;
	int id;
	public int tx, ty;
	
	public Item()
	{
		attr = new Attributes();
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
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Item)) return false;
		return ((Item) obj).id == id;
	}
	
	public void draw(int x, int y, int size, Graphics2D g)
	{
		Helper.setRenderingHints(g, false);
		Helper.drawImage(Game.getImage("system/icons.png"), x, y, size, size, tx * 24, ty * 24, 24, 24, g);
		Helper.setRenderingHints(g, true);
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
}
