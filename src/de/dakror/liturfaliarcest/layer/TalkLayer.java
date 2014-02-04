package de.dakror.liturfaliarcest.layer;

import java.awt.Graphics2D;

import org.json.JSONArray;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.liturfaliarcest.game.entity.Entity;

/**
 * @author Dakror
 */
public class TalkLayer extends Layer
{
	Entity source;
	JSONArray talk;
	int index;
	
	@Override
	public void init()
	{}
	
	@Override
	public void draw(Graphics2D g)
	{}
	
	@Override
	public void update(int tick)
	{}
	
	public void setTalk(JSONArray t, Entity s)
	{
		talk = t;
		source = s;
	}
}
