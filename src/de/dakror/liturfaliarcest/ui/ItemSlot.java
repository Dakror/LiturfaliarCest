package de.dakror.liturfaliarcest.ui;

import java.awt.Graphics2D;

import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.item.ItemStack;

/**
 * @author Dakror
 */
public class ItemSlot extends ClickableComponent
{
	public static int SIZE = 64;
	
	ItemStack itemStack;
	
	public ItemSlot(int x, int y)
	{
		super(x, y, SIZE, SIZE);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.drawContainer(x, y, width, height, false, false, false, g);
		
		drawItemStack(g);
	}
	
	public void drawItemStack(Graphics2D g)
	{
		if (itemStack != null)
		{
			itemStack.getItem().draw(x, y, width, g);
			if (itemStack.getAmount() > 1) Helper.drawRightAlignedString(itemStack.getAmount() + "", x + width, y + height - 5, g, 30);
		}
	}
	
	@Override
	public void update(int tick)
	{}
	
	public ItemStack getItemStack()
	{
		return itemStack;
	}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g)
	{
		if (itemStack == null) return;
		Helper.drawShadow(x, y - 64, g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(itemStack.getItem().getName()) + 30, 64, g);
		Helper.drawString(itemStack.getItem().getName(), x + 15, y - 24, g, 30);
	}
	
	public void setItemStack(ItemStack itemStack)
	{
		this.itemStack = itemStack;
	}
}
