package de.dakror.liturfaliarcest.game.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;

public class World extends Layer
{
	public static final int TILE_SIZE = 64;
	
	int x, y;
	
	String name;
	Area bump;
	
	public World(String name)
	{
		this.name = name;
		x = y = 0;
	}
	
	@Override
	public void init()
	{
		Game.getImage("maps/" + name + "/" + name + "-0.png");
		Game.getImage("maps/" + name + "/" + name + "-1.png");
		
		// creating bump
		BufferedImage bumpImage = Game.getImage("maps/" + name + "/" + name + "-2.png");
		bump = new Area();
		for (int i = 0; i < bumpImage.getWidth(); i++)
			for (int j = 0; j < bumpImage.getHeight(); j++)
				if (new Color(bumpImage.getRGB(i, j)).equals(Color.white)) bump.add(new Area(new Rectangle(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE)));
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.setRenderingHints(g, false);
		
		BufferedImage img = Game.getImage("maps/" + name + "/" + name + "-0.png");
		g.drawImage(img, x, y, img.getWidth() / 32 * TILE_SIZE, img.getHeight() / 32 * TILE_SIZE, Game.w);
		
		img = Game.getImage("maps/" + name + "/" + name + "-1.png");
		g.drawImage(img, x, y, img.getWidth() / 32 * TILE_SIZE, img.getHeight() / 32 * TILE_SIZE, Game.w);
		
		Helper.setRenderingHints(g, true);
		
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(x, y);
		g.setTransform(at);
		
		drawComponents(g);
		
		g.setTransform(old);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
	
	public void addEntity(Entity e)
	{
		components.add(e);
	}
}
