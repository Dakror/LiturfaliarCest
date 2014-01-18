package de.dakror.liturfaliarcest.game.entity.creature;

import java.awt.Graphics2D;

import de.dakror.liturfaliarcest.game.entity.Entity;

/**
 * @author Dakror
 */
public class Creature extends Entity
{
	protected String tex;
	
	public Creature(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}
	
	@Override
	public void draw(Graphics2D g)
	{}
	
	@Override
	protected void tick(int tick)
	{}
}
