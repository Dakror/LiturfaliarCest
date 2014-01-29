package de.dakror.liturfaliarcest.game.entity.object;

import java.awt.Graphics2D;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;
import de.dakror.liturfaliarcest.game.item.Item;
import de.dakror.liturfaliarcest.game.world.World;

/**
 * @author Dakror
 */
public class ItemDrop extends Entity
{
	Item item;
	
	public ItemDrop(int x, int y, Item item)
	{
		super(x + (World.TILE_SIZE - 32) / 2, y + (World.TILE_SIZE - 32) / 2, 32, 32);
		this.item = item;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("system/itemdropshadow.png"), x, y, width, height, Game.w);
		item.draw(x, y, width, g);
	}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g)
	{
		Helper.drawShadow(x, y, g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(item.getName()) + 30, 64, g);
		Helper.drawString(item.getName(), x + 15, y + 40, g, 30);
	}
	
	@Override
	protected void tick(int tick)
	{}
}
