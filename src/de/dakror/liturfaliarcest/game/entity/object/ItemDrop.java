package de.dakror.liturfaliarcest.game.entity.object;

import java.awt.Graphics2D;

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
	protected void tick(int tick)
	{}
	
}
