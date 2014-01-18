package de.dakror.liturfaliarcest.game.entity.creature;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;

/**
 * @author Dakror
 */
public abstract class Creature extends Entity
{
	protected String tex;
	int startTick, frame;
	
	public Creature(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.setRenderingHints(g, false);
		
		BufferedImage img = Game.getImage("char/chars/" + tex + ".png");
		Helper.drawImage(img, x, y, width, height, frame * img.getWidth() / 4, 0, img.getWidth() / 4, img.getHeight() / 4, g);
		
		Helper.setRenderingHints(g, true);
	}
	
	@Override
	protected void tick(int tick)
	{
		if (target != null && startTick == 0)
		{
			startTick = tick;
			return;
		}
		
		if (target != null && (tick - startTick) % 15 == 0) frame = (frame + 1) % 4;
	}
}
