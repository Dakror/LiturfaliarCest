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
 

package de.dakror.liturfaliarcest.game.entity.object;

import java.awt.Graphics2D;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;
import de.dakror.liturfaliarcest.game.item.Item;
import de.dakror.liturfaliarcest.game.item.ItemStack;
import de.dakror.liturfaliarcest.game.world.World;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class ItemDrop extends Entity {
	Item item;
	
	public ItemDrop(int x, int y, JSONObject meta) throws JSONException {
		super(x + (World.TILE_SIZE - 32) / 2, y + (World.TILE_SIZE - 32) / 2, 32, 32, meta);
		item = Item.getItemInstance(meta.getInt("itemID"));
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(Game.getImage("system/itemdropshadow.png"), x, y, width, height, Game.w);
		item.draw(x, y, width, g);
	}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g) {
		Helper.drawShadow(x, y, g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(item.getName()) + 30, 64, g);
		Helper.drawString(item.getName(), x + 15, y + 40, g, 30);
	}
	
	public Item getItem() {
		return item;
	}
	
	public ItemStack getItemStack() {
		return new ItemStack(item);
	}
	
	@Override
	protected void tick(int tick) {}
}
