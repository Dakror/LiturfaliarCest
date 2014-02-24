package de.dakror.liturfaliarcest.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.liturfaliarcest.game.Game;

/**
 * @author Dakror
 */
public class QuestLayer extends Layer
{
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		BufferedImage book = Game.getImage("system/book.png");
		g.drawImage(book, (Game.getWidth() - book.getWidth()) / 2, (Game.getHeight() - book.getHeight()) / 2, Game.w);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void init()
	{
		modal = true;
	}
}
