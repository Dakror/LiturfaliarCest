/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 

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
