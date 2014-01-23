package de.dakror.liturfaliarcest.game.entity.object;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;
import de.dakror.liturfaliarcest.game.world.World;

/**
 * @author Dakror
 */
public class Object extends Entity
{
	ObjectType type;
	
	public Object(int x, int y, ObjectType type)
	{
		super(x * (World.TILE_SIZE / 32), y * (World.TILE_SIZE / 32), type.width * (World.TILE_SIZE / 32), type.height * (World.TILE_SIZE / 32));
		
		this.type = type;
		
		bumpX = type.bumpX * (World.TILE_SIZE / 32);
		bumpY = type.bumpY * (World.TILE_SIZE / 32);
		bumpWidth = type.bumpWidth * (World.TILE_SIZE / 32);
		bumpHeight = type.bumpHeight * (World.TILE_SIZE / 32);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.setRenderingHints(g, false);
		
		Composite c = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		
		BufferedImage img = Game.getImage("tiles/" + type.tileset);
		Helper.drawImage(img, x, y, width, height, type.tx, type.ty, type.width, type.height, g);
		
		g.setComposite(c);
		
		Helper.setRenderingHints(g, true);
	}
	
	@Override
	protected void tick(int tick)
	{
		if (getArea().intersects(Game.player.getArea()) && Game.player.getY() + Game.player.bumpY + Game.player.bumpHeight < y + bumpY + bumpHeight) alpha = 0.8f;
		else alpha = 1;
	}
}
