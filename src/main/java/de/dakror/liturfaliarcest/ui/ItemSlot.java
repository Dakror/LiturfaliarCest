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
 

package de.dakror.liturfaliarcest.ui;

import java.awt.Graphics2D;

import de.dakror.liturfaliarcest.game.item.ItemStack;
import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class ItemSlot extends ClickableComponent {
	public static int SIZE = 64;
	
	ItemStack itemStack;
	
	public ItemSlot(int x, int y) {
		super(x, y, SIZE, SIZE);
	}
	
	@Override
	public void draw(Graphics2D g) {
		Helper.drawContainer(x, y, width, height, false, false, false, g);
		
		drawItemStack(g);
	}
	
	public void drawItemStack(Graphics2D g) {
		if (itemStack != null) {
			itemStack.getItem().draw(x, y, width, g);
			if (itemStack.getAmount() > 1) Helper.drawRightAlignedString(itemStack.getAmount() + "", x + width, y + height - 5, g, 30);
		}
	}
	
	@Override
	public void update(int tick) {}
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g) {
		if (itemStack == null) return;
		Helper.drawShadow(x, y - 64, g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(itemStack.getItem().getName()) + 30, 64, g);
		Helper.drawString(itemStack.getItem().getName(), x + 15, y - 24, g, 30);
	}
	
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
}
