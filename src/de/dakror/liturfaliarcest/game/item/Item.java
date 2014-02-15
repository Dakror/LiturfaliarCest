package de.dakror.liturfaliarcest.game.item;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

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
	private static HashMap<Integer, Item> items;
	
	public static int SIZE = 32;
	
	public static void init()
	{
		items = new HashMap<>();
		
		CSVReader csv = new CSVReader("/csv/items.csv");
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
				case 4:
					item.stack = cell.length() == 0 ? 1 : Integer.parseInt(cell);
					break;
			}
			
			if (cell.length() == 0) continue;
			
			String h = headings[csv.getIndex()];
			if (Character.isUpperCase((h.charAt(0)))) item.attr.set(Attribute.valueOf(h), Float.parseFloat(cell));
		}
		
		items.put(item.id, item);
	}
	
	public static Item getItemForId(int id)
	{
		return items.get(id);
	}
	
	public static Item getItemInstance(int id)
	{
		return items.get(id).clone();
	}
	
	private String name;
	private Attributes attr;
	private int id;
	private int stack;
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
	
	public int getStack()
	{
		return stack;
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
	
	public Icon getIcon(int size)
	{
		BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		draw(0, 0, size, (Graphics2D) bi.getGraphics());
		
		return new ImageIcon(bi);
	}
	
	@Override
	public Item clone()
	{
		Item i = new Item();
		i.name = new String(name);
		i.id = id;
		i.stack = stack;
		i.tx = tx;
		i.ty = ty;
		return i;
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
}
