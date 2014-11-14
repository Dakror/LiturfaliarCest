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
