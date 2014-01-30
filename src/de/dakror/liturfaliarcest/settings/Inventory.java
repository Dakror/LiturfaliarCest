package de.dakror.liturfaliarcest.settings;

import org.json.JSONArray;
import org.json.JSONException;

import de.dakror.liturfaliarcest.game.item.Item;
import de.dakror.liturfaliarcest.game.item.ItemStack;

/**
 * @author Dakror
 */
public class Inventory
{
	ItemStack[][] slots;
	
	public Inventory(int w, int h)
	{
		slots = new ItemStack[h][w];
	}
	
	public Inventory(JSONArray data) throws JSONException
	{
		slots = new ItemStack[data.length()][data.getJSONArray(0).length()];
		for (int i = 0; i < data.length(); i++)
		{
			JSONArray row = data.getJSONArray(i);
			for (int j = 0; j < row.length(); j++)
			{
				JSONArray c = row.getJSONArray(j);
				slots[i][j] = c.length() == 0 ? null : new ItemStack(Item.items.get(c.getInt(0)), c.getInt(1));
			}
		}
	}
	
	public void setSlot(int x, int y, ItemStack stack)
	{
		slots[y][x] = stack;
	}
	
	public void clearSlot(int x, int y)
	{
		slots[y][x] = null;
	}
	
	public ItemStack getSlot(int x, int y)
	{
		return slots[y][x];
	}
	
	public JSONArray getData()
	{
		JSONArray a = new JSONArray();
		for (ItemStack[] s1 : slots)
		{
			JSONArray r = new JSONArray();
			for (ItemStack s : s1)
				r.put(s == null ? new JSONArray() : s.getData());
			a.put(r);
		}
		return a;
	}
}
