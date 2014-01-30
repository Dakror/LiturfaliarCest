package de.dakror.liturfaliarcest.game.item;

import org.json.JSONArray;

/**
 * @author Dakror
 */
public class ItemStack
{
	Item item;
	int amount;
	
	public ItemStack(Item item, int amount)
	{
		this.item = item;
		this.amount = amount;
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public void addAmount(int amount)
	{
		this.amount += amount;
	}
	
	public boolean isFull()
	{
		return amount == item.getStack();
	}
	
	public JSONArray getData()
	{
		JSONArray a = new JSONArray();
		a.put(item.getId());
		a.put(amount);
		return a;
	}
}
