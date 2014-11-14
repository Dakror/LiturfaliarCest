package de.dakror.liturfaliarcest.game.item;

import org.json.JSONArray;

/**
 * @author Dakror
 */
public class ItemStack {
	Item item;
	int amount;
	
	public ItemStack(Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}
	
	public ItemStack(Item item) {
		this(item, 1);
	}
	
	public Item getItem() {
		return item;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void addAmount(int amount) {
		this.amount += amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public boolean isFull() {
		return amount == item.getStack();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemStack) return item.equals(((ItemStack) obj).item) && amount == ((ItemStack) obj).amount;
		return false;
	}
	
	public JSONArray getData() {
		JSONArray a = new JSONArray();
		a.put(item.getId());
		a.put(amount);
		return a;
	}
}
