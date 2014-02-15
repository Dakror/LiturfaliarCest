package de.dakror.liturfaliarcest.game.animation;

import java.awt.Graphics2D;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliarcest.game.entity.Entity;

/**
 * @author Dakror
 */
public class AnimationSpot extends Entity
{
	Animation anim;
	
	public AnimationSpot(int x, int y, JSONObject meta) throws JSONException
	{
		super(x, y, 0, 0, meta);
		anim = Animation.getAnimationInstance(meta.getInt("animID"));
		width = meta.has("width") ? meta.getInt("width") : anim.getDefaultWidth();
		height = meta.has("height") ? meta.getInt("height") : anim.getDefaultHeight();
		anim.init(width, height, meta.has("smooth") ? meta.getBoolean("smooth") : false, meta.has("endless") ? meta.getBoolean("endless") : true);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		anim.draw(x, y, g);
	}
	
	@Override
	protected void tick(int tick)
	{
		anim.update(tick);
		if (anim.isDone()) kill();
	}
}
