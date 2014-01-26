package de.dakror.liturfaliarcest.game.entity.creature;

import java.awt.image.BufferedImage;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.EntityType;
import de.dakror.liturfaliarcest.game.world.World;

/**
 * @author Dakror
 */
public class NPC extends Creature
{
	EntityType type;
	JSONObject meta;
	
	public NPC(int x, int y, EntityType type, JSONObject meta) throws JSONException
	{
		super(x, y, 0, 0);
		this.meta = meta;
		tex = meta.getString("texture");
		BufferedImage img = Game.getImage(tex);
		width = img.getWidth() / 4 * (World.TILE_SIZE / 32);
		height = img.getHeight() / 4 * (World.TILE_SIZE / 32);
		bumpY = Math.round(70 * height / 96f);
		bumpX = Math.round(16 * width / 64f);
		bumpWidth = width / 2;
		bumpHeight = Math.round(24 * height / 96f);
	}
}
