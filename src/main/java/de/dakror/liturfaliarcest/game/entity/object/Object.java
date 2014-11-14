package de.dakror.liturfaliarcest.game.entity.object;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;
import de.dakror.liturfaliarcest.game.entity.EntityType;
import de.dakror.liturfaliarcest.game.world.World;

/**
 * @author Dakror
 */
public class Object extends Entity {
	EntityType type;
	
	public Object(int x, int y, EntityType type, JSONObject meta) {
		super(x, y, type.width * (World.TILE_SIZE / 32), type.height * (World.TILE_SIZE / 32), meta);
		
		this.type = type;
		
		bumpX = type.bumpX * (World.TILE_SIZE / 32);
		bumpY = type.bumpY * (World.TILE_SIZE / 32);
		bumpWidth = type.bumpWidth * (World.TILE_SIZE / 32);
		bumpHeight = type.bumpHeight * (World.TILE_SIZE / 32);
		
		try {
			if (meta.has("bx")) bumpX = meta.getInt("bx");
			if (meta.has("by")) bumpX = meta.getInt("by");
			if (meta.has("bw")) bumpX = meta.getInt("bw");
			if (meta.has("bh")) bumpX = meta.getInt("bh");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (type.tileset.equals("black")) return;
		
		Helper.setRenderingHints(g, false);
		
		Composite c = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		
		BufferedImage img = Game.getImage("tiles/" + type.tileset);
		Helper.drawImage(img, x, y, width, height, type.tx, type.ty, type.width, type.height, g);
		
		g.setComposite(c);
		
		Helper.setRenderingHints(g, true);
		
		if (getBump().contains(Game.player.getBump())) Game.player.draw(g);
	}
	
	@Override
	protected void tick(int tick) {
		if (type.tileset.equals("black")) return;
		
		if (getArea().intersects(Game.player.getArea()) && Game.player.getY() + Game.player.bumpY + Game.player.bumpHeight < y + bumpY + bumpHeight && !getBump().contains(Game.player.getBump())) alpha = 0.6f;
		else alpha = 1;
	}
}
