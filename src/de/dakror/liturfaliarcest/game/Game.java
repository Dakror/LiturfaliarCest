package de.dakror.liturfaliarcest.game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.world.World;

public class Game extends GameFrame
{
	public static Game currentGame;
	public static World world;
	
	public Game()
	{
		currentGame = this;
	}
	
	@Override
	public void initGame()
	{
		world = new World("map0");
		addLayer(world);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawLayers(g);
		
		Helper.drawString("FPS: " + getFPS(), 10, 26, g, 18);
		Helper.drawString("UPS: " + getUPS(), 10, 52, g, 18);
	}
	
	@Override
	public BufferedImage loadImage(String p)
	{
		BufferedImage img = super.loadImage(p);
		
		if (img == null) return null;
		
		if (p.startsWith("maps/") && !p.endsWith("-2.png"))
		{
			img = Helper.toBufferedImage(img.getScaledInstance(img.getWidth() / 32 * World.TILE_SIZE, img.getHeight() / 32 * World.TILE_SIZE, BufferedImage.SCALE_FAST));
		}
		
		return img;
	}
}
