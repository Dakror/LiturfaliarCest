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
				slots[i][j] = c.length() == 0 ? null : new ItemStack(Item.getItemInstance(c.getInt(0)), c.getInt(1));
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
	
	public int getWidth()
	{
		return slots[0].length;
	}
	
	public int getHeight()
	{
		return slots.length;
	}
	
	public boolean put(ItemStack stack)
	{
		for (int i = 0; i < slots.length; i++) // y
		{
			for (int j = 0; j < slots[0].length; j++) // x
			{
				if (slots[i][j] != null && slots[i][j].getItem().equals(stack.getItem()) && !slots[i][j].isFull())
				{
					int prev = slots[i][j].getAmount();
					slots[i][j].setAmount(Math.min(slots[i][j].getAmount() + stack.getAmount(), stack.getItem().getStack()));
					stack.addAmount(prev - slots[i][j].getAmount());
					
					if (stack.getAmount() == 0) return true;
				}
			}
		}
		
		for (int i = 0; i < slots.length; i++)
		{
			for (int j = 0; j < slots[0].length; j++)
			{
				if (slots[i][j] == null)
				{
					slots[i][j] = stack;
					return true;
				}
			}
		}
		
		return false;
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
