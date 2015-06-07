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

import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class ToolbarItemSlot extends ItemSlot {
	public ToolbarItemSlot(int x, int y) {
		super(x, y);
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (state != 0) Helper.drawOutline(x, y, width, height, state == 1, g);
		
		drawItemStack(g);
	}
}
