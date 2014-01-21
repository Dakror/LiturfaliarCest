package de.dakror.liturfaliarcest.game.entity.object;

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
		
		bumpX = type.bumpX;
		bumpY = type.bumpY;
		bumpWidth = type.bumpWidth;
		bumpHeight = type.bumpHeight;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.setRenderingHints(g, false);
		
		BufferedImage img = Game.getImage("tiles/" + type.tileset);
		Helper.drawImage(img, x, y, width, height, type.tx, type.ty, type.width, type.height, g);
		
		Helper.setRenderingHints(g, true);
	}
	
	@Override
	protected void tick(int tick)
	{}
}
